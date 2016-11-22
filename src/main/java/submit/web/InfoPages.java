package submit.web;

// $Id: InfoPages.java,v 1.15 2016-11-01 10:10:45-04 ericholp Exp $

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import org.springframework.core.env.Environment;

import submit.entity.OrgInfo;

@Controller
public class InfoPages {

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: InfoPages.java,v 1.15 2016-11-01 10:10:45-04 ericholp Exp $";

    @Resource
    private Environment env;

    // ------------------------------------------------------------------------
    @RequestMapping("/Faq")
    public String Faq(ModelMap model){
	model.addAttribute("banner", (System.currentTimeMillis() / 1000l) % 6 + 1);
	model.addAttribute("page", "Faq");
	OrgInfo org = new OrgInfo();
	org.setEmail(env.getRequiredProperty("org.email"));
	org.setPhone(env.getRequiredProperty("org.phone"));
	org.setName(env.getRequiredProperty("org.name"));
	org.setNamefull(env.getRequiredProperty("org.namefull"));
	model.addAttribute("org", org);
        return "Faq";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/About")
    public String About(ModelMap model){
	model.addAttribute("banner", (System.currentTimeMillis() / 1000l) % 6 + 1);
	model.addAttribute("page", "About");
        return "About";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/Help")
    public String Help(ModelMap model){
	model.addAttribute("banner", (System.currentTimeMillis() / 1000l) % 6 + 1);
	OrgInfo org = new OrgInfo();
	org.setEmail(env.getRequiredProperty("org.email"));
	org.setPhone(env.getRequiredProperty("org.phone"));
	org.setName(env.getRequiredProperty("org.name"));
	org.setNamefull(env.getRequiredProperty("org.namefull"));
	model.addAttribute("org", org);
	model.addAttribute("page", "Help");
        return "Help";
    }
}
