package submit.impl;

// $Id: Auth.java,v 1.1 2016-11-22 17:17:07-04 ericholp Exp $

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.io.PrintWriter;

import javax.servlet.http.HttpSession;
import org.springframework.ui.ModelMap;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Auth {

    private final static Logger LOGGER = Logger.getLogger(Auth.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: Auth.java,v 1.1 2016-11-22 17:17:07-04 ericholp Exp $";

    // ------------------------------------------------------------------------
    private boolean kauth(String username, String password){
	try {

	    Process pb = new ProcessBuilder("/usr/bin/kinit", username).start(); // worked
	    PrintWriter out = new PrintWriter(pb.getOutputStream());
            out.println(password);
	    out.flush();
	    out.close();

	    pb.waitFor();
	    
            int shellExitStatus = pb.exitValue();
            if(shellExitStatus == 0){
                Runtime.getRuntime().exec("/usr/bin/kdestroy");
                return true;
            }
            
            return false;
	} catch(OutOfMemoryError ex) {
	    LOGGER.log(Level.SEVERE, null, ex);
            return false;
	} catch (IOException ex) {
	    LOGGER.log(Level.SEVERE, null, ex);
            return false;
	} catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }	
    }
    
    // ------------------------------------------------------------------------
    public boolean authenticate(String username, String password) {

	String hostname = "";
	try {
	    hostname = InetAddress.getLocalHost().getHostName();
	}catch(UnknownHostException ex){
	    LOGGER.log(Level.SEVERE, null, ex);
	}
	
	if(hostname.matches(".*local.*")){
	    String[] usernames = {"test"};
	    String[] passwords = {"test"};
	    
	    int i = 0;
	    for(String usernameinlist : usernames){
		if(usernameinlist.equals(username) && passwords[i].equals(password))
		    return true;
		i++;
	    }
	    return false;
	}

	return kauth(username, password);
    }    

    // ------------------------------------------------------------------------
    public boolean fullauth(String username, String password, ModelMap model, HttpSession session, boolean itsnew){

	if(session == null){
	    model.addAttribute("badauth", "1");
	    model.addAttribute("loginmessage", "System error");	
	    LOGGER.log(Level.SEVERE, "authenicate failed for username={0} due to a null session", new Object[]{username});
	    return false;
	}
	
	session.setMaxInactiveInterval(-1);
	session.setAttribute("loggedin", "0");
	session.setAttribute("name", "");
	session.setAttribute("username", "");
	session.setAttribute("email", "");
	session.setAttribute("isadmin", "0");

	if(username == null){
	    LOGGER.log(Level.INFO, "authenicate failed due to a null username");
	    model.addAttribute("badauth", "1");
	    model.addAttribute("loginmessage", "Please enter a netid");
	    return false;
	}

	if(password == null){
	    LOGGER.log(Level.INFO, "authenicate failed due to a null password for username={0}", new Object[]{username});
	    model.addAttribute("badauth", "1");
	    model.addAttribute("loginmessage", "Please enter a password");
	    return false;
	}
	
	if(authenticate(username, password)){
	    model.addAttribute("badauth", "0");
	    return true;
	}

	model.addAttribute("badauth", "1");
	model.addAttribute("loginmessage", "Please try again");
	LOGGER.log(Level.INFO, "authenicate failed for username={0}", new Object[]{username});
	return false;
    }
}
