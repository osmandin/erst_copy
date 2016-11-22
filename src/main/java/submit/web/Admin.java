package submit.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import submit.impl.Utils;

@Controller
public class Admin {
    private final static Logger LOGGER = Logger.getLogger(Admin.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: Admin.java,v 1.4 2016-10-25 00:09:30-04 ericholp Exp $";

    // ------------------------------------------------------------------------
    @RequestMapping("/Admin")
    public String Admin(
			ModelMap model,
			HttpSession session
			){
	LOGGER.log(Level.INFO, "Admin");

	Utils utils = new Utils();
	if(!utils.setupAdminHandler(model, session)){
	    return "Home";
	}

	model.addAttribute("page", "Admin");
        return "Admin";
    }

}
