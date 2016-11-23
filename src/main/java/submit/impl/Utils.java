package submit.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;

public class Utils {
    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: Utils.java,v 1.261 2016-11-23 14:25:55-04 ericholp Exp $";
    
    private final static Logger LOGGER = Logger.getLogger(Utils.class.getCanonicalName());

    // ------------------------------------------------------------------------
    public boolean setupAuthdHandler(ModelMap model, HttpSession session){
	
	long banner = (System.currentTimeMillis() / 1000l) % 6 + 1;
	model.addAttribute("banner", banner);
	
	if(session != null){
	    session.setMaxInactiveInterval(-1);
	    return true;
	}else{
	    return false;
	}
    }

    // ------------------------------------------------------------------------
    public boolean setupAdminHandler(ModelMap model, HttpSession session){
	if ( session.isNew() ) {
	    return false;
	}
	
	session.setMaxInactiveInterval(-1);
	
	if(session.getAttribute("loggedin") == null || session.getAttribute("loggedin").toString().equals("0") || session.getAttribute("isadmin") == null || session.getAttribute("isadmin").toString().equals("0")){
	    return false;
	}
	
	return true;
    }
    
    // ------------------------------------------------------------------------
    public boolean isAcceptedAddress(HttpServletRequest request, String addressmatch){
    	String remoteAddr = request.getRemoteAddr();
	if(!remoteAddr.matches(addressmatch)){
	    LOGGER.log(Level.SEVERE, "Error: Not an accepted address: {0}", new Object[]{remoteAddr});
	    return false;
	}
	return true;
    }

    // ------------------------------------------------------------------------
    public String getHostname(){
	try {
	    return InetAddress.getLocalHost().getHostName();
	}catch(UnknownHostException ex){
	    return "unknown";
	}
    }

    // ------------------------------------------------------------------------
    public void redirectToRoot(
			       ServletContext context,
			       HttpServletResponse response
			       ){
	try{
	    response.sendRedirect(context.getContextPath());
	}catch(java.io.IOException ex){
	    LOGGER.log(Level.SEVERE, "Redirect Error: ", ex);
	}
    }    
}
