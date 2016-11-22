package submit.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import submit.email.Email;
import submit.entity.ApprovedRsasForm;
import submit.entity.EmailSetup;
import submit.entity.RsaFileDataForm;
import submit.entity.RsasForm;
import submit.entity.SsaContactsForm;
import submit.impl.Utils;
import submit.repository.ApprovedRsasFormRepository;
import submit.repository.RsaFileDataFormRepository;
import submit.repository.RsasFormRepository;
import submit.repository.SsaContactsFormRepository;
import submit.service.RsasFormService;
import submit.service.ApprovedRsasFormService;
import submit.service.UsersFormService;

@Controller
public class RsaAdmin {
    private final static Logger LOGGER = Logger.getLogger(RsaAdmin.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: RsaAdmin.java,v 1.25 2016-11-08 02:40:23-04 ericholp Exp $";

    @Resource
    private Environment env;

    @Autowired
    ServletContext context;
    
    private static VelocityEngine velocityEngine;

    @Autowired
    public void setVelocityEngine(VelocityEngine ve){
	velocityEngine = ve;
    }

    private JavaMailSenderImpl sender;

    @Autowired
    public void setSender(JavaMailSenderImpl sender){	
	this.sender = sender;
    }

    @Autowired
    private RsasFormRepository rsarepo;

    @Autowired
    private RsaFileDataFormRepository filedatarepo;

    @Autowired
    private ApprovedRsasFormRepository approvedrsarepo;

    @Autowired
    private RsasFormService rsaservice;

    @Autowired
    private UsersFormService userservice;
    
    @Autowired
    private SsaContactsFormRepository contactrepo;

    @Autowired
    ApprovedRsasFormService approvedrsaservice;
    
    // ------------------------------------------------------------------------
    @RequestMapping("/ListDraftRsas")
    public String ListDraftRsas(
				ModelMap model,
				@RequestParam(value="rsaid", required=false) String rsaid,
				@RequestParam(value="downloadfailed", required=false) String downloadfailed,
				HttpSession session
				){
	LOGGER.log(Level.INFO, "ListDraftRsas");

	Utils utils = new Utils();	
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	model.addAttribute("rsaid", rsaid);
	model.addAttribute("downloadfailed", downloadfailed);

	List<RsasForm> rsasForms= rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);

	return "ListDraftRsas";
    }
    
    // ------------------------------------------------------------------------
    @RequestMapping("/ListApprovedRsas")
    public String ListApprovedRsas(
				   ModelMap model,
				   @RequestParam(value="rsaid", required=false) String rsaid,
				   @RequestParam(value="downloadfailed", required=false) String downloadfailed,
				   HttpSession session
				   ){
	LOGGER.log(Level.INFO, "ListAppovedRsas");

	Utils utils = new Utils();	
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	model.addAttribute("rsaid", rsaid);
	model.addAttribute("downloadfailed", downloadfailed);

	List<RsasForm> rsasForms= rsarepo.findByApprovedTrueOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);

	return "ListApprovedRsas";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditDraftRsa", method=RequestMethod.GET)
    public String EditDraftRsa(
			       ModelMap model,
			       @RequestParam(value="rsaid", required=false) int rsaid,
			       HttpSession session
			       ){
	LOGGER.log(Level.INFO, "EditDraftRsa Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(rsaid <= 0){
	    return "Home";
	}

	model.addAttribute("defaultaccessrestriction", env.getRequiredProperty("defaults.accessrestriction"));

	RsasForm rsasForm = rsarepo.findById(rsaid);

	model.addAttribute("rsasForm", rsasForm);
	
	model.addAttribute("action", "EditDraftRsa");
	return "EditDraftRsa";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditDraftRsa", method=RequestMethod.POST)
    public String EditDraftRsa(
			       ModelMap model,
			       RsasForm rsasForm,
			       HttpServletRequest request,
			       HttpServletResponse response,
			       HttpSession session
			       ){
	LOGGER.log(Level.INFO, "EditDraftRsa Post");

	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(session == null){
	    LOGGER.log(Level.SEVERE, "null session");
	    return "Home";
	}
	
	if(rsasForm == null){
	    LOGGER.log(Level.SEVERE, "rsasForm is null");
	    return "Home";
	}

	model.addAttribute("defaultaccessrestriction", env.getRequiredProperty("defaults.accessrestriction"));
	

	rsaservice.saveForm(rsasForm);
	
	String name = (String)session.getAttribute("name");
	String emailaddr = (String)session.getAttribute("email");
	
	String useremailaddress = "\"" + name.trim() + "\" <" + emailaddr.trim() + ">";
	
	EmailSetup emailsetup = new EmailSetup();
	
	emailsetup.setFrom(env.getRequiredProperty("submit.from"));
	emailsetup.setSubject("Updated Draft Transfer Request");
	emailsetup.setTo(useremailaddress);

	Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
	email.EditDraftRsaSendToUser(rsasForm);
	
	model.addAttribute("name", name);	    
	model.addAttribute("emailsent", "1");
    
	model.addAttribute("success", "1");
	
	if(rsasForm.isApproved()){
	    LOGGER.log(Level.INFO, "draft RSA approved");

	    List<SsaContactsForm> contactinfo = rsasForm.getSsasForm().getSsaContactsForms();
	    
	    if(contactinfo!=null && contactinfo.size() > 0){
		LOGGER.log(Level.INFO, "sending approved email");
		
		String[] emailrecipts = new String[contactinfo.size()];
		String sep = "";	    
		StringBuilder sb = new StringBuilder();
		int i=0;
		for(SsaContactsForm contact : contactinfo){
		    emailrecipts[i++] = "\"" + contact.getName() + "\" <" + contact.getEmail() + ">";
		    sb.append(sep + "\"" + contact.getName() + "\" <" + contact.getEmail() + ">");
		    sep = ", ";
		}
		
		emailsetup = new EmailSetup();
		emailsetup.setSubject("Draft Transfer Request Approved");
		emailsetup.setToarray(emailrecipts);
		emailsetup.setFrom(env.getRequiredProperty("submit.from"));
		emailsetup.setUsername(session.getAttribute("username").toString());
		
		email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
		email.ApprovedDraftRsaSendToContacts(rsasForm);

	    }
		
	    model.addAttribute("edited", "1");
	    
	    List<RsasForm> rsasForms = rsarepo.findByApprovedTrueOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);
	    
	    return "ListApprovedRsas";
	}

	model.addAttribute("rsasForm", rsasForm);
	
	model.addAttribute("action", "EditDraftRsa");
	return "EditDraftRsa";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditApprovedRsa", method=RequestMethod.GET)
    public String EditApprovedRsa(
				  ModelMap model,
				  HttpServletRequest request,
				  @RequestParam(value="rsaid", required=false) int rsaid,	
				  HttpSession session
				  ){
	LOGGER.log(Level.INFO, "EditAppovedRsas Get");
	
	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(rsaid <= 0){
	    LOGGER.log(Level.SEVERE, "rsaid <= 0");
	    return "Home";
	}

	model.addAttribute("defaultaccessrestriction", env.getRequiredProperty("defaults.accessrestriction"));
	
	RsasForm rsasForm = rsarepo.findById(rsaid);
	model.addAttribute("rsasForm", rsasForm);

	model.addAttribute("action", "EditApprovedRsa");
	return "EditApprovedRsa";
    }
    
    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditApprovedRsa", method=RequestMethod.POST)
    public String EditApprovedRsa(
				  ModelMap model,
				  RsasForm rsasForm,
				  HttpServletRequest request,				  
				  HttpSession session
				  ){
	LOGGER.log(Level.INFO, "EditAppovedRsas Post");

	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(rsasForm == null){
	    LOGGER.log(Level.SEVERE, "rsasForm is null");
	    return "Home";
	}

	model.addAttribute("defaultaccessrestriction", env.getRequiredProperty("defaults.accessrestriction"));

	rsaservice.saveForm(rsasForm);
	
	if(!rsasForm.isApproved()){
	    model.addAttribute("edited", "1");
	    
	    List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);

	    return "ListDraftRsas";
	}

	model.addAttribute("rsasForm", rsasForm);
	
	model.addAttribute("action", "EditApprovedRsa");
	return "EditApprovedRsa";
    }    

    // ------------------------------------------------------------------------
    @RequestMapping("/DeleteDraftRsa")
    public String DeleteDraftRsa(
				 ModelMap model,
				 @RequestParam(value="rsaid", required=false) int rsaid,
				 HttpServletRequest request,
				 HttpSession session
				 ){
	LOGGER.log(Level.INFO, "DeleteDraftRsa");

	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(rsaid <= 0){
	    LOGGER.log(Level.SEVERE, "rsaid <= 0");
	    return "Home";
	}

	String dropoff = env.getRequiredProperty("dropoff.dir");
	if(dropoff == null || dropoff.equals("")){
	    LOGGER.log(Level.SEVERE, "dropoff is null");
	    return "Home";
	}

	try {
	    FileUtils.deleteDirectory(new File(String.format(dropoff + "/" + rsaid)));
	}catch(IOException ex){
	    LOGGER.log(Level.SEVERE, "Error: ", ex);
	}
	
	RsasForm rsa = rsarepo.findById(rsaid);

	String staffemails = env.getRequiredProperty("org.email");
	String[] emailrecipts = staffemails.split(",");
	
	EmailSetup emailsetup = new EmailSetup();
	emailsetup.setSubject("Draft Transfer Request deleted: " + rsaid);
	emailsetup.setToarray(emailrecipts);
	emailsetup.setFrom(env.getRequiredProperty("submit.from"));
	emailsetup.setUsername(session.getAttribute("username").toString());

	Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
	email.DeleteDraftRsaSendToStaff(rsa);

	
	List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
	for(RsaFileDataForm fd : fds){
	    filedatarepo.delete(fd);
	}
	rsarepo.delete(rsa);
	
	model.addAttribute("onedeleted", "1");
	model.addAttribute("onedeletedrsaid", rsaid);

	List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);
	
	return "ListDraftRsas";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/DeleteApprovedRsa")
    public String DeleteApprovedRsa(
				    ModelMap model,
				    @RequestParam(value="rsaid", required=false) int rsaid,
				    HttpSession session
				    ){
	LOGGER.log(Level.INFO, "DeleteApprovedRsa");
	
	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	if(rsaid <= 0){
            LOGGER.log(Level.SEVERE, "rsaid <= 0");
	    return "Home";
	}
	
	String dropoff = env.getRequiredProperty("dropoff.dir");
	if(dropoff == null || dropoff.equals("")){
	    LOGGER.log(Level.SEVERE, "dropoff is null");
	    return "Home";
	}

	RsasForm rsa = rsarepo.findById(rsaid);

	approvedrsaservice.recordDeletedRsa(rsa, session);
	
	List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
	for(RsaFileDataForm fd : fds){
	    filedatarepo.delete(fd);
	}
	rsarepo.delete(rsa);
	
	try {
	    FileUtils.deleteDirectory(new File(String.format(dropoff + "/" + rsaid)));
	}catch(IOException ex){
	    LOGGER.log(Level.SEVERE, "Error: ", ex);
	}
	
	model.addAttribute("onedeleted", "1");
	model.addAttribute("onedeletedrsaid", rsaid);

	List<RsasForm> rsasForms = rsarepo.findByApprovedTrueOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);
		
	return "ListApprovedRsas";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/DeleteDraftRsas")
    public String DeleteDraftRsas(
				  ModelMap model,
				  @RequestParam(value="rsa", required=false) int[] rsas,
				  HttpServletRequest request,
				  HttpSession session
				  ){
	LOGGER.log(Level.INFO, "DeleteDraftRsas");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}
	
	String dropoff = env.getRequiredProperty("dropoff.dir");
	if(dropoff == null || dropoff.equals("")){
            LOGGER.log(Level.SEVERE, "dropoff is null");
	    List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);	    
	    return "ListDraftRsas";
	}

	if(rsas == null){
	    model.addAttribute("nodeletes", "1");
	    List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);	    
	    return "ListDraftRsas";
	}

	List<RsasForm> deletedrsas  = new ArrayList<RsasForm>();
	for(int rsaid : rsas){
	    if(rsaid > 0){
		RsasForm rsa = rsarepo.findById(rsaid);
		deletedrsas.add(rsa);
	    }
	}
	
	int numdeleted=0;
	String deletersaids = "";
	String sep = "";
	for(int rsaid : rsas){
	    
	    if(rsaid > 0){
		
		deletersaids += sep + rsaid;
		sep = ", ";
		
		model.addAttribute("weredeletes", "1");
		model.addAttribute("deletersaids", deletersaids);		
			
		RsasForm rsa = rsarepo.findById(rsaid);
		List<RsaFileDataForm> fds =  rsa.getRsaFileDataForms();
		for(RsaFileDataForm fd : fds){
		    filedatarepo.delete(fd);
		}
		rsarepo.delete(rsa);
		numdeleted++;
		
		try {
		    FileUtils.deleteDirectory(new File(dropoff + "/" + rsaid));
		}catch(IOException ex){
		    LOGGER.log(Level.SEVERE, null, ex);
		}
	    }
	}

	if(numdeleted <= 0){
	    List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);
	
	    return "ListDraftRsas";
	}
	
	String staffemails = env.getRequiredProperty("org.email");
	String[] emailrecipts = staffemails.split(",");
	
	EmailSetup emailsetup = new EmailSetup();
	emailsetup.setSubject("Draft Transfer Request(s) deleted: " + deletersaids);
	emailsetup.setToarray(emailrecipts);
	emailsetup.setFrom(env.getRequiredProperty("submit.from"));
	emailsetup.setUsername(session.getAttribute("username").toString());

	Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
	email.DeleteDraftRsasSendToStaff(deletedrsas);


	List<RsasForm> rsasForms = rsarepo.findByApprovedFalseOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);
	
	return "ListDraftRsas";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/DeleteApprovedRsas")
    public String DeleteApprovedRsas(
				     ModelMap model,
				     @RequestParam(value="rsa", required=false) int[] rsaids,
				     HttpServletRequest request,
				     HttpSession session
				     ){
	LOGGER.log(Level.INFO, "DeleteApprovedRsas");
	
	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	String dropoff = env.getRequiredProperty("dropoff.dir");
	if(dropoff == null || dropoff.equals("")){
	    LOGGER.log(Level.SEVERE, "dropoff is null");
	    return "Home";
	}

	if(rsaids == null){
	    model.addAttribute("nodeletes", "1");
	    
	    List<RsasForm> rsasForms = rsarepo.findByApprovedTrueOrderByTransferdateAsc();
	    model.addAttribute("rsasForms", rsasForms);
	    
	    return "ListApprovedRsas";
	}
	
	String deletersaids = "";
	String sep = "";
	for(int rsaid : rsaids){
	    if(rsaid <= 0){
		LOGGER.log(Level.SEVERE, "rsaid <= 0");
		continue;
	    }

	    deletersaids += sep + rsaid;
	    sep = ", ";
	    
	    model.addAttribute("weredeletes", "1");
	    model.addAttribute("deletersaids", deletersaids);		
	    
	    RsasForm rsa = rsarepo.findById(rsaid);
	    List<RsaFileDataForm> fds = rsa.getRsaFileDataForms();
	    for(RsaFileDataForm fd : fds){
		filedatarepo.delete(fd);
	    }

	    approvedrsaservice.recordDeletedRsa(rsa, session);

	    rsarepo.delete(rsa);
	    
	    try {
		FileUtils.deleteDirectory(new File(dropoff + "/" + rsaid));
	    }catch(IOException ex){
		LOGGER.log(Level.SEVERE, null, ex);
	    }
	}
    
	List<RsasForm> rsasForms = rsarepo.findByApprovedTrueOrderByTransferdateAsc();
	model.addAttribute("rsasForms", rsasForms);
	
	return "ListApprovedRsas";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/ApprovedRsaLog")
    public String ApprovedRsaLog(
				 ModelMap model,
				 HttpSession session
				 ){
	LOGGER.log(Level.INFO, "ApprovedRsaLog");

	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	List<ApprovedRsasForm> rsas = approvedrsarepo.findAllOrderByDatetimeAsc();
	model.addAttribute("data", rsas);

	return "ApprovedRsaLog";
    }
}
