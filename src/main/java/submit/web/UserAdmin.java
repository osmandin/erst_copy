package submit.web;

// $Id: UserAdmin.java,v 1.12 2016-11-01 13:16:56-04 ericholp Exp $

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import submit.email.Email;
import submit.entity.DepartmentsForm;
import submit.entity.EmailSetup;
import submit.entity.IdKey;
import submit.entity.MapForm;
import submit.entity.OrgInfo;
import submit.entity.SubmitAppInfo;
import submit.entity.UsersForm;
import submit.impl.Utils;
import submit.repository.DepartmentsFormRepository;
import submit.repository.MapFormRepository;
import submit.repository.UsersFormRepository;
import submit.service.DepartmentsFormService;
import submit.service.UsersFormService;

@Controller
public class UserAdmin {
    private final static Logger LOGGER = Logger.getLogger(UserAdmin.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: UserAdmin.java,v 1.12 2016-11-01 13:16:56-04 ericholp Exp $";

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
    private UsersFormRepository userrepo;

    @Autowired
    private DepartmentsFormRepository departmentrepo;

    @Autowired
    MapFormRepository maprepo;

    @Autowired
    private UsersFormService userservice;
    
    @Autowired
    DepartmentsFormService departmentservice;

    // ------------------------------------------------------------------------
    @RequestMapping(value="/ListUsers", method=RequestMethod.GET)
    public String ListUsers(
			    ModelMap model,
			    HttpSession session
			    ){
	LOGGER.log(Level.INFO, "ListUsers Get");

	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	Map<Integer, Map<Integer, Boolean>> adminactivemap = new HashMap<Integer, Map<Integer, Boolean>>();	
	List<UsersForm> adminusersForms = userrepo.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
	for(UsersForm uf : adminusersForms){
	    List<DepartmentsForm> dfs = uf.getDepartmentsForms();
	    if(dfs != null){ 
		Map<Integer, Boolean> dmap = new HashMap<Integer, Boolean>();
		for(DepartmentsForm df : dfs){
		    IdKey ik = new IdKey();
		    ik.userid=uf.getId();
		    ik.departmentid=df.getId();

		    MapForm mf = maprepo.findByKey(ik);
		    dmap.put(df.getId(), mf.isDepartmentactive());
		}
		adminactivemap.put(uf.getId(), dmap);
	    }
	}
        model.addAttribute("adminactivemap", adminactivemap);
        model.addAttribute("adminusersForms", adminusersForms);
	
	Map<Integer, Map<Integer, Boolean>> nonadminactivemap = new HashMap<Integer, Map<Integer, Boolean>>();	
	List<UsersForm> nonadminusersForms = userrepo.findByIsadminFalseOrderByLastnameAscFirstnameAsc();
	for(UsersForm uf : nonadminusersForms){
	    List<DepartmentsForm> dfs = uf.getDepartmentsForms();
	    if(dfs != null){
		Map<Integer, Boolean> dmap = new HashMap<Integer, Boolean>();
		for(DepartmentsForm df : dfs){
		    IdKey ik = new IdKey();
		    ik.userid=uf.getId();
		    ik.departmentid=df.getId();

		    MapForm mf = maprepo.findByKey(ik);
		    dmap.put(df.getId(), mf.isDepartmentactive());
		}
		nonadminactivemap.put(uf.getId(), dmap);
	    }
	}
        model.addAttribute("nonadminactivemap", nonadminactivemap);
        model.addAttribute("nonadminusersForms", nonadminusersForms);
	
        return "ListUsers";
    }

    // ------------------------------------------------------------------------    
    @RequestMapping(value="/EditUser", method=RequestMethod.GET)
    public String EditUser(
			   ModelMap model,
			   @RequestParam(value="userid", required=false) int userid,
			   HttpSession session
			   ){
	LOGGER.log(Level.INFO, "EditUser Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	model.addAttribute("userid", userid);

	List<DepartmentsForm> dfs = departmentservice.findSkipUserid(userid);
	model.addAttribute("dropdowndepartments", dfs);
	
	UsersForm usersForm = userrepo.findById(userid);
	model.addAttribute("usersForm", usersForm);

	return "EditUser";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditUser", method=RequestMethod.POST)
    public ModelAndView EditUser(
				 UsersForm usersForm,
				 BindingResult result,
				 final RedirectAttributes redirectAttributes,
				 @RequestParam(value="delete", required=false) String delete,
				 @RequestParam(value="department_id", required=false) DepartmentsForm selectedDepartmentsForms,
				 ModelMap model,
				 HttpSession session
				 ){
	LOGGER.log(Level.INFO, "EditUser Post");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return new ModelAndView("/Home");
	}
	
	if(result.hasErrors()){
	    LOGGER.log(Level.INFO, "EditUser Post: has errors");
	    return new ModelAndView("/EditUser");
	}

	if(delete != null){
	    String username = usersForm.getUsername();
	    // otherwise departments will be removed from map table for other users
	    usersForm.setDepartmentsForms(null);
	    userrepo.delete(usersForm);
	    userrepo.delete(usersForm); // first delete only deletes depts
	    redirectAttributes.addFlashAttribute("userdeleted", true);
	    redirectAttributes.addFlashAttribute("username", username);
	}else{

	    List<MapForm> mfstop = maprepo.findAll(); // why needed? (see below)
	    
	    int userid = usersForm.getId();

	    if(selectedDepartmentsForms == null){
		LOGGER.log(Level.INFO, "selected department null");
	    }else{
		List<DepartmentsForm> ds = usersForm.getDepartmentsForms();
		if(ds == null){
		    List<DepartmentsForm> newdfs  = new ArrayList<DepartmentsForm>();
		    newdfs.add(selectedDepartmentsForms);
		    usersForm.setDepartmentsForms(newdfs);
		    userrepo.save(usersForm);
		}else{
		    ds.add(selectedDepartmentsForms);
		    usersForm.setDepartmentsForms(ds);
		    userrepo.save(usersForm);
		}
	    }
	    
	    String thisusername = usersForm.getUsername();
	    
	    boolean found = false;
	    List<UsersForm> UsersForms = userrepo.findByUsername(thisusername);
	    for(UsersForm uf : UsersForms){
		if(uf.getId() != userid && thisusername.equals(uf.getUsername())){
		    found = true;
		    break;
		}
	    }
	    if(found){
		ModelAndView mav = new ModelAndView();
		
		LOGGER.log(Level.INFO, "EditUser Post: Duplicate usernames are not allowed!!");
		
		model.addAttribute("error", "Duplicate usernames are not allowed.");
		mav.setViewName("/EditUser");
		
		return mav;
	    }

	    List<DepartmentsForm> dfs = usersForm.getDepartmentsForms();

	    userrepo.save(usersForm);
	    
	    maprepo.save(mfstop);  // needed because all departments not present in template as hidden variables... could do it but this is easier

	    if(dfs == null){
		LOGGER.log(Level.INFO, "DepartmentsForm null");
	    }else{		    
		for(DepartmentsForm df : dfs){
		    
		    IdKey ik = new IdKey();
		    ik.userid=usersForm.getId();
		    ik.departmentid=df.getId();
		    
		    MapForm mf = maprepo.findByKey(ik);
		    
		    if(mf == null){
			LOGGER.log(Level.INFO, "mapform null");
		    }else{			
			mf.setDepartmentactive(df.isActive());
			maprepo.save(mf);
		    }
		}
	    }
	    
	    redirectAttributes.addFlashAttribute("usermodified", true);
	    redirectAttributes.addFlashAttribute("username", usersForm.getUsername());
	}

	
	ModelAndView mav = new ModelAndView();
	
	if(delete != null){
	    List<UsersForm> adminusersForms = userrepo.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
	    model.addAttribute("adminusersForms", adminusersForms);
	    
	    List<UsersForm> usersForms = userservice.findAllNonadmin();
	    model.addAttribute("nonadminusersForms", usersForms);
	    
	    mav.setViewName("redirect:/ListUsers");
	}else{
	    List<DepartmentsForm> dfs = departmentservice.findSkipUserid(usersForm.getId());
	    model.addAttribute("dropdowndepartments", dfs);

	    usersForm = userrepo.findById(usersForm.getId());
	    model.addAttribute("usersForm", usersForm);

	    mav.setViewName("/EditUser");
	}

	return mav;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/AddUser", method=RequestMethod.GET)
    public String AddUser(
			  UsersForm usersForm,
			  ModelMap model,
			  HttpSession session
			  ){
	LOGGER.log(Level.INFO, "AddUser Get");

	Utils utils = new Utils();  
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}
	
	List<DepartmentsForm> dfs = departmentrepo.findAll();
	//model.addAttribute("departmentsForm", dfs);

	usersForm.setDepartmentsForms(dfs);

        return "AddUser";
    }
    
    // ------------------------------------------------------------------------
    @RequestMapping(value="/AddUser", method=RequestMethod.POST)
    public ModelAndView AddUser(
				UsersForm usersForm,
				BindingResult result,
				final RedirectAttributes redirectAttributes,
				@RequestParam(value="department_id", required=false) DepartmentsForm[] selectedDepartmentsForms,
				@RequestParam(value="department_active", required=false) int[] activedepts,				
				ModelMap model,
				HttpSession session
				){
	LOGGER.log(Level.INFO, "AddUser Post");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return new ModelAndView("/Home");
	}
	
	if(result.hasErrors()){
	    LOGGER.log(Level.INFO, "AddUser Post: has errors");
	    return new ModelAndView("/AddUser");
	}

	List<UsersForm> uf = userrepo.findByUsername(usersForm.getUsername());
	if(uf != null && uf.size() != 0){
	    LOGGER.log(Level.INFO, "duplicate user with this username: size={0}", new Object[]{uf.size()});

	    List<DepartmentsForm> dfs = departmentrepo.findAll();
	    usersForm.setDepartmentsForms(dfs);
		
	    model.addAttribute("duplicate", 1);
	    
	    return new ModelAndView("/AddUser");
	}
	
	int userid = usersForm.getId();
	
	if(selectedDepartmentsForms == null){
	    LOGGER.log(Level.INFO, "selectedDepartmentsForms is null");
	}else{

	    Map<Integer, Boolean> active = new HashMap<Integer, Boolean>();
	    for(int activedept : activedepts){
		active.put(activedept, true);
	    }	    
	    
	    List<DepartmentsForm> newdfs  = new ArrayList<DepartmentsForm>();
	    int cnt=0;
	    for(DepartmentsForm df : selectedDepartmentsForms){
		newdfs.add(df);
		cnt++;
	    }
	    if(cnt > 0){
		usersForm.setDepartmentsForms(newdfs);
	    }
	}
	
	usersForm = userrepo.save(usersForm);

	ModelAndView mav = new ModelAndView();
	String message = "New User " + usersForm.getId() + " was successfully created.";

	session.setAttribute("userid", Integer.toString(usersForm.getId()));

	LOGGER.log(Level.INFO, "add user: userid={0}", new Object[]{usersForm.getId()});
	
	mav.setViewName("redirect:/NotifyUser");
	
	redirectAttributes.addFlashAttribute("message", message);

	redirectAttributes.addFlashAttribute("userid", userid);

	return mav;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DeleteUser", method=RequestMethod.POST)
    public ModelAndView DeleteUser(
				   @RequestParam(value="userid", required=false) int userid,
				   final RedirectAttributes redirectAttributes,
				   ModelMap model,
				   HttpSession session
				   ){
	LOGGER.log(Level.INFO, "DeleteUser Post");
	
	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return new ModelAndView("/Home");
	}
	
	UsersForm usersForm = userrepo.findById(userid);
	
	String username = usersForm.getUsername();
	
	usersForm.setDepartmentsForms(null);
	userrepo.delete(usersForm);
	userrepo.delete(usersForm);
	
	ModelAndView mav = new ModelAndView();
	
	mav.setViewName("redirect:/ListUsers");
	
	redirectAttributes.addFlashAttribute("userdeleted", true);
	redirectAttributes.addFlashAttribute("username", username);

	LOGGER.log(Level.INFO, "delete user: username={0}", new Object[]{username});
	
	return mav;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DeleteUserDepartment", method=RequestMethod.GET)
    public String DeleteUserDepartment(
				       @RequestParam(value="id", required=false) int id,
				       @RequestParam(value="userid", required=false) int userid,
				       ModelMap model,
				       HttpSession session
				       ){
	LOGGER.log(Level.INFO, "DeleteUserDepartment Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}
	
	model.addAttribute("userid", userid);
	
	UsersForm uf = userrepo.findById(userid);
	List<DepartmentsForm> dfs = uf.getDepartmentsForms();

	LOGGER.log(Level.INFO, "delete departmentsForms id={0}", new Object[]{id});

	List<DepartmentsForm> newdfs  = new ArrayList<DepartmentsForm>();
	for(DepartmentsForm df : dfs){
	    if(df.getId() != id){
		newdfs.add(df);
	    }
	}
	uf.setDepartmentsForms(newdfs);
	userrepo.save(uf);
	
	dfs = departmentservice.findSkipUserid(userid);
	model.addAttribute("dropdowndepartments", dfs);
	
	UsersForm usersForm = userrepo.findById(userid);
	model.addAttribute("usersForm", usersForm);

	return "EditUser";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/NotifyUser", method=RequestMethod.GET)
    public String NotifyUser(
			     ModelMap model,
			     @RequestParam(value="userid", required=false) String useridin,
			     HttpServletRequest request,
			     HttpSession session
			     ){
	LOGGER.log(Level.INFO, "NotifyUser Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	int userid = -1;
	if(useridin == null){
	    if(session.getAttribute("userid") != null && session.getAttribute("userid").toString() != null){
		userid = Integer.parseInt(session.getAttribute("userid").toString());
		if(userid <= 0){
		    LOGGER.log(Level.SEVERE, "NotifyUserGet: userid={0}", new Object[]{userid});
		    return "NotifyUser";
		}
	    }
	}else{
	    userid = Integer.parseInt(useridin);
	}
	
	if(userid <= 0){
	    LOGGER.log(Level.SEVERE, "NotifyUser Get: userid={0}", new Object[]{userid});
	    return "NotifyUser";
	}
	
	LOGGER.log(Level.INFO, "NotifyUser Get: userid={0}", new Object[]{userid});

	UsersForm user = userrepo.findById(userid);
	if(user == null){
	    LOGGER.log(Level.SEVERE, "NotifyUser Get: usersForm is null");
	    return "NotifyUser";
	}

	List<DepartmentsForm> departments = user.getDepartmentsForms();

	SubmitAppInfo submitappinfo = new SubmitAppInfo();
	submitappinfo.setName(env.getRequiredProperty("submit.name"));
	submitappinfo.setFrom(env.getRequiredProperty("submit.from"));
	submitappinfo.setRoot(context.getContextPath());
	
	Map<String, Object> vemodel = new HashMap<String, Object>();
	vemodel.put("user", user);
	vemodel.put("departments", departments);
	vemodel.put("session", session);
	vemodel.put("thishost", utils.getHostname());
	vemodel.put("submit", submitappinfo);
	

	String emailsubject = "Account Created for " + env.getRequiredProperty("submit.name");
	String useremailaddress = "\"" + user.getFirstname().trim() + " " + user.getLastname().trim() + "\" <" + user.getEmail().trim() + ">";
	
	EmailSetup emailsetup = new EmailSetup();
	emailsetup.setSubject(emailsubject);
	emailsetup.setTo(useremailaddress);
	emailsetup.setFrom(env.getRequiredProperty("submit.from"));
	emailsetup.setFirstname(user.getFirstname());
	emailsetup.setLastname(user.getLastname());
	emailsetup.setIsadmin(user.isIsadmin());
	emailsetup.setUsername(user.getUsername());

	OrgInfo orginfo = new OrgInfo();
	orginfo.setEmail(env.getRequiredProperty("org.email"));
	orginfo.setPhone(env.getRequiredProperty("org.phone"));
	orginfo.setName(env.getRequiredProperty("org.name"));
	orginfo.setNamefull(env.getRequiredProperty("org.namefull"));

	vemodel.put("emailsetup", emailsetup);
	vemodel.put("org", orginfo);
	
	String emailtext = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/NotifyUserText.vm", "UTF8", vemodel);

	emailsetup.setBody(emailtext);

	model.addAttribute("emailsetup", emailsetup);

	return "NotifyUser";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/NotifyUser", method=RequestMethod.POST)
    public String NotifyUser(
			     ModelMap model,
			     EmailSetup emailsetup,
			     
			     HttpServletRequest request,
			     HttpSession session
			     ){
	LOGGER.log(Level.INFO, "NotifyUser Post");
	
	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}


	if(emailsetup == null){
	    LOGGER.log(Level.SEVERE, "NotifyUser: emailssetup is null");
	    model.addAttribute("emailsent", "0");
	    model.addAttribute("failed", "1");
	    model.addAttribute("emailrecips", "unknown email address");
	    return "NotifyUser";
	}
	    
	LOGGER.log(Level.INFO, "NotifyUser: emailsetup: to={0}", new Object[]{emailsetup.getTo()});

	model.addAttribute("emailsetup", emailsetup);

	model.addAttribute("emailsent", "0");
	model.addAttribute("failed", "1");
	model.addAttribute("invalidaddresses", "0");
	model.addAttribute("emailrecips", emailsetup.getTo());
	
	Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
	email.NotifyUser();

	return "NotifyUser";
    }
}
