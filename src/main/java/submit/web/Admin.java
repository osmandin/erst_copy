package submit.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

import submit.impl.Utils;

@Controller
public class Admin {
    private final static Logger LOGGER = Logger.getLogger(Admin.class.getCanonicalName());

    @Resource
    private Environment env;

    // ------------------------------------------------------------------------
    @RequestMapping("/Admin")
    public String Admin(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "Admin");

        Utils utils = new Utils();
        if (!utils.setupAdminHandler(model, session, env)) {
            return "Home";
        }

        model.addAttribute("page", "Admin");
        return "Admin";
    }

}
