package submit.web;

// $Id: SubmissionAgreementFormPages.java,v 1.15 2016-11-15 11:55:16-04 ericholp Exp $

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import submit.captcha.VerifyRecaptcha;
import submit.entity.SubmitData;
import submit.entity.OrgInfo;
import submit.impl.Utils;
import submit.service.SubmissionAgreementService;
import submit.service.DepartmentsFormService;

@Controller
public class SubmissionAgreementFormPages {
    private final static Logger LOGGER = Logger.getLogger(SubmissionAgreementFormPages.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: SubmissionAgreementFormPages.java,v 1.15 2016-11-15 11:55:16-04 ericholp Exp $";

    @Resource
    private Environment env;

    @Autowired
    private SubmissionAgreementService submissionagreementservice;

    @Resource
    private DepartmentsFormService departmentservice;
    
    // ------------------------------------------------------------------------
    @RequestMapping(value="/SubmitRequest", method=RequestMethod.GET)
    public String SubmitRequest(
				ModelMap model,
				HttpServletRequest request,
				HttpSession session
				){
	LOGGER.log(Level.INFO, "SubmitRequest Get");
	
	model.addAttribute("duplicatedept", "0");
	
	if(session.getAttribute("captchacorrect") != null && !session.getAttribute("captchacorrect").toString().equals("1")){
	    return "Home";
	}

	Utils utils = new Utils();
	String validaddressmatch = env.getRequiredProperty("validaddressmatch");
	if(!utils.isValidAddress(request, validaddressmatch)){
	    LOGGER.log(Level.SEVERE, "Not a valid address");
	    return "Home";
	}

	String staffemail = env.getRequiredProperty("org.email");
	model.addAttribute("staff_email", staffemail);

	String publickey = env.getRequiredProperty("publiccaptchakey");	
	model.addAttribute("publickey", publickey);

	OrgInfo org = new OrgInfo();
	org.setEmail(env.getRequiredProperty("org.email"));
	org.setPhone(env.getRequiredProperty("org.phone"));
	org.setName(env.getRequiredProperty("org.name"));
	org.setNamefull(env.getRequiredProperty("org.namefull"));
	model.addAttribute("org", org);
	
	SubmitData submitData = new SubmitData();
	model.addAttribute("submitData", submitData);
	
	return "SubmissionAgreementForm";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value="/SubmitRequest", method=RequestMethod.POST)
    public String SubmitRequest(
				ModelMap model,
				SubmitData submitData,
				@RequestParam(value="g-recaptcha-response", required=false) String response,
				HttpServletRequest request,
				HttpSession session
				){
	LOGGER.log(Level.INFO, "SubmitRequest Post");
	
	model.addAttribute("duplicatedept", "0");
	
	if(session.getAttribute("captchacorrect") != null && !session.getAttribute("captchacorrect").toString().equals("1")){
	    return "Home";
	}
		
	Utils utils = new Utils();
	String validaddressmatch = env.getRequiredProperty("validaddressmatch");
	if(!utils.isValidAddress(request, validaddressmatch)){
	    LOGGER.log(Level.SEVERE, "Not a valid address");
	    return "Home";
	}

	String staffemail = env.getRequiredProperty("org.email");
	model.addAttribute("staff_email", staffemail);

	model.addAttribute("correct", "0");
	
	String publickey = env.getRequiredProperty("publiccaptchakey");	
	model.addAttribute("publickey", publickey);

	OrgInfo org = new OrgInfo();
	org.setEmail(env.getRequiredProperty("org.email"));
	org.setPhone(env.getRequiredProperty("org.phone"));
	org.setName(env.getRequiredProperty("org.name"));
	org.setNamefull(env.getRequiredProperty("org.namefull"));
	model.addAttribute("org", org);
	
	model.addAttribute("submitrecap", "1");
	
	if(response == null){
	    model.addAttribute("correct", "0");
	    model.addAttribute("submitData", submitData);
	    return "SubmissionAgreementForm";
	}
	
	String privatekey = env.getRequiredProperty("privatecaptchakey");
	
	boolean captchacorrect = VerifyRecaptcha.verify(response, privatekey);
	
	if(captchacorrect){
	    model.addAttribute("correct", "1");
	    session.setAttribute("captchacorrect", "1");

	    // this checks for a duplicate department name... maybe just check for a department that is assigned to an ssa...?
	    if(departmentservice.isDuplicate(submitData.getDepartment().trim())){
		model.addAttribute("duplicatedept", "1");
		model.addAttribute("submitData", submitData);
		LOGGER.log(Level.SEVERE, "duplicate department={0}", new Object[]{submitData.getDepartment()});
		return "SubmissionAgreementForm";
	    }
	    
	    submissionagreementservice.handleSubmissionAgreementForm(submitData, request, session, model);
	    
	    return "Home";
	}

	model.addAttribute("correct", "0");
	return "SubmissionAgreementForm";
    }
}
