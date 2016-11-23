package submit.web;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import submit.entity.DepartmentsForm;
import submit.entity.SsasForm;
import submit.entity.UsersForm;
import submit.impl.Utils;
import submit.repository.DepartmentsFormRepository;
import submit.repository.SsasFormRepository;
import submit.repository.UsersFormRepository;

@Controller
public class DepartmentAdmin {
    private final static Logger LOGGER = Logger.getLogger(DepartmentAdmin.class.getCanonicalName());

    @Autowired
    private UsersFormRepository userrepo;

    @Autowired
    private DepartmentsFormRepository departmentrepo;

    @Autowired
    private SsasFormRepository ssarepo;

    @Resource
    private Environment env;

    // ------------------------------------------------------------------------
    @RequestMapping(value="/AddDepartment", method=RequestMethod.POST)
    public String AddDepartment(
				ModelMap model,
				@RequestParam("name") String departmentname,
				HttpSession session){
	LOGGER.log(Level.INFO, "AddDepartment Post");
	
	Utils utils = new Utils();	    
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	DepartmentsForm df = new DepartmentsForm();
	df.setName(departmentname);
	departmentrepo.save(df);
	
	List<DepartmentsForm> departmentsForms = departmentrepo.findAllOrderByNameAsc();
        model.addAttribute("departmentsForms", departmentsForms);

	return "ListDepartments";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DeleteDepartment", method=RequestMethod.POST)
    public String DeleteDepartment(
				   ModelMap model,
				   @RequestParam("departmentid") int departmentid,
				   HttpSession session
				   ){
	LOGGER.log(Level.INFO, "DeleteDepartment Post");
	
	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}
	
	model.addAttribute("departmentid", departmentid);

	List<SsasForm> ssas = ssarepo.findAllForDepartmentId(departmentid);

	DepartmentsForm df = departmentrepo.findById(departmentid);
	List<UsersForm> users = df.getUsersForms();
	
	if(ssas.size() > 0 || users.size() > 0){
	    String dependentssas = "";
	    StringBuilder sb = new StringBuilder();
	    int dependentssacnt = 0;
	    String sep = "";
	    if(ssas.size() > 0){
		for(SsasForm ssa : ssas){
		    sb.append(sep + ssa.getId());
		    sep = ", ";
		    dependentssacnt++;
		}
		dependentssas = sb.toString();
	    }
	    String dependentusers = "";
	    sb = new StringBuilder();
	    int dependentusercnt = 0;
	    sep = "";
	    if(users.size() > 0){
		for(UsersForm user : users){
		    sb.append(sep + user.getFirstname() + " " + user.getLastname());
		    sep = ", ";
		    dependentusercnt++;
		}
		dependentusers = sb.toString();
	    }
	    model.addAttribute("notdeleted", "1");
	    String departmentname = df.getName();;
	    if(departmentname != null){
		model.addAttribute("departmentname", departmentname);
	    }
	    if(dependentssacnt > 0){
		model.addAttribute("dependentssas", dependentssas);
		model.addAttribute("weredependentssas", "1");
	    }
	    if(dependentusercnt > 0){
		model.addAttribute("dependentusers", dependentusers);
		model.addAttribute("weredependentusers", "1");
	    }
	}else{
	    String departmentname = df.getName();
	    if(departmentname != null){
		model.addAttribute("departmentname", departmentname);
	    }
	    departmentrepo.delete(df);
	    model.addAttribute("deleted", "1");
	}

	List<DepartmentsForm> departmentsForms = departmentrepo.findAllOrderByNameAsc();
        model.addAttribute("departmentsForms", departmentsForms);
	
	return "ListDepartments";
    }

    // ------------------------------------------------------------------------    
    @RequestMapping(value="/ListDepartments", method=RequestMethod.GET)
    public String ListDepartments(
				  ModelMap model,
				  HttpSession session
				  ){
	LOGGER.log(Level.INFO, "ListDepartments Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	List<DepartmentsForm> departmentsForms = departmentrepo.findAllOrderByNameAsc();
        model.addAttribute("departmentsForms", departmentsForms);
	
	return "ListDepartments";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditDepartment", method=RequestMethod.GET)
    public String EditDepartment(
				 ModelMap model,
				 @RequestParam("departmentid") int departmentid,
				 HttpSession session
				 ){
	LOGGER.log(Level.INFO, "EditDepartment Get");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	if(departmentid <= 0){
	    LOGGER.log(Level.SEVERE, "departmentid <= 0");
	    model.addAttribute("departmentiderror", "1");
	    return "ListDepartments";
	}
	
	DepartmentsForm departmentsForm = departmentrepo.findById(departmentid);
	model.addAttribute("departmentsForm", departmentsForm);

	return "EditDepartment";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/EditDepartment", method=RequestMethod.POST)
    public String EditDepartment(
				 DepartmentsForm departmentsForm,
				 BindingResult result,
				 final RedirectAttributes redirectAttributes,
				 ModelMap model,
				 HttpSession session
				 ){
	LOGGER.log(Level.INFO, "EditDepartment Post");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	departmentsForm = departmentrepo.save(departmentsForm);
	model.addAttribute("departmentsForm", departmentsForm);
	model.addAttribute("departmentid", departmentsForm.getId());

	List<UsersForm> users = departmentsForm.getUsersForms();

	List<SsasForm> ssas = ssarepo.findAllForDepartmentId(departmentsForm.getId());
	model.addAttribute("ssas", ssas);

	if(ssas.size() > 0 || users.size() > 0){
	    model.addAttribute("dependencies", "1");
	}else{
	    model.addAttribute("dependencies", "0");
	}
	
	return "EditDepartment";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DepartmentDeleteWarning", method=RequestMethod.POST)
    public String DepartmentDeleteWarning(
					  ModelMap model,
					  @RequestParam("departmentid") int departmentid,
					  HttpSession session
					  ){
	LOGGER.log(Level.INFO, "DepartmentDeleteWarning Post");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	DepartmentsForm df = departmentrepo.findById(departmentid);
	
	if(departmentid == 0){
	    departmentid = Integer.parseInt(session.getAttribute("departmentid").toString());
	}else{
	    session.setAttribute("departmentid", departmentid);
	}
	model.addAttribute("departmentid", departmentid);

	model.addAttribute("departmentsForm", df);
	
	String departmentname = df.getName();
	model.addAttribute("departmentname", departmentname);
	
	List<UsersForm> users = df.getUsersForms();
	
	for(UsersForm uf : users){
	    LOGGER.log(Level.INFO, "  userid={0}", new Object[]{uf.getId()});
	    List<DepartmentsForm> dfs = uf.getDepartmentsForms();
	    for(DepartmentsForm dfi : dfs){
		LOGGER.log(Level.INFO, "     df id={0}", new Object[]{dfi.getId()});
	    }
	}
		
	model.addAttribute("users", users);
	
	List<SsasForm> ssas = ssarepo.findAllForDepartmentId(departmentid);
	model.addAttribute("ssas", ssas);

	if(users.isEmpty() && ssas.isEmpty()){
	    model.addAttribute("departmentid", departmentid);
	    model.addAttribute("departmentname", departmentname);
	    model.addAttribute("dependenciesresolved", "1");
	    model.addAttribute("dependencies", "0");
	    return "EditDepartment";
	}
	
	return "DepartmentDeleteWarning";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DepartmentDeleteWarningDeleteUsers", method=RequestMethod.POST)
    public String DepartmentDeleteWarningDeleteUsers(
						     ModelMap model,
						     @RequestParam("departmentid") int departmentid,
						     @RequestParam(value="userids", required=false) int[] userids,
						     HttpSession session
						     ){
	LOGGER.log(Level.INFO, "DepartmentDeleteWarningDeleteUsers Post");
	
	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	
	if(departmentid <= 0){
	    departmentid = (Integer)session.getAttribute("departmentid");
	}else{
	    session.setAttribute("departmentid", departmentid);
	}
	model.addAttribute("departmentid", departmentid);

	if(userids == null){
	    model.addAttribute("nouserdeletes", "1");
	}else{
	    String deletedusers = "";
	    String sep = "";
	    int deletedcnt = 0;
	    for(int userid : userids){
		UsersForm uf = userrepo.findById(userid);
		if(uf != null){
		    userrepo.delete(uf);
		    deletedusers = sep + userid;
		    sep = ", ";
		    deletedcnt++;
		}
	    }
	    model.addAttribute("usersdeleted", "1");
	    model.addAttribute("deletedusers", deletedusers);
	    model.addAttribute("deletedcnt", deletedcnt);
	}

	DepartmentsForm df = departmentrepo.findById(departmentid);
		
	List<UsersForm> users = df.getUsersForms();
	model.addAttribute("users", users);
	
	List<SsasForm> ssas = ssarepo.findAllForDepartmentId(departmentid);
	model.addAttribute("ssas", ssas);

	if(users.isEmpty() && ssas.isEmpty()){
	    model.addAttribute("departmentid", departmentid);
	    model.addAttribute("departmentname", df.getName());
	    model.addAttribute("dependenciesresolved", "1");
	    model.addAttribute("dependencies", "0");
	    return "EditDepartment";
	}
	
	return "DepartmentDeleteWarning";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/DepartmentDeleteWarningDeleteUsers", method=RequestMethod.GET)
    public String DepartmentDeleteWarningDeleteUsers(
						     ModelMap model,
						     @RequestParam("departmentid") int departmentid,
						     HttpSession session
						     ){
	LOGGER.log(Level.INFO, "DepartmentDeleteWarningDeleteUsers Get");
	
	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session, env)){
	    return "Home";
	}

	if(departmentid <= 0){
	    departmentid = (Integer)session.getAttribute("departmentid");
	}else{
	    session.setAttribute("departmentid", departmentid);
	}
	model.addAttribute("departmentid", departmentid);

	DepartmentsForm df = departmentrepo.findById(departmentid);
		
	List<UsersForm> users = df.getUsersForms();
	model.addAttribute("users", users);

	List<SsasForm> ssas = ssarepo.findAllForDepartmentId(departmentid);
	model.addAttribute("ssas", ssas);

	if(users.isEmpty() && ssas.isEmpty()){
	    model.addAttribute("departmentid", departmentid);
	    model.addAttribute("departmentname", df.getName());
	    model.addAttribute("dependenciesresolved", "1");
	    model.addAttribute("dependencies", "0");
	    return "EditDepartment";
	}

	return "DepartmentDeleteWarning";
    }
}
