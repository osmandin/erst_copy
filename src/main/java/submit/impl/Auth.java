package submit.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.io.PrintWriter;

import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.core.env.Environment;

import java.util.logging.Level;
import java.util.logging.Logger;


// osm added:

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

public class Auth {

    private final static Logger LOGGER = Logger.getLogger(Auth.class.getCanonicalName());

    @SuppressWarnings("unused")
    private static final String rcsinfo = "$Id: Auth.java,v 1.3 2016-11-23 16:13:45-04 ericholp Exp $";

    // ------------------------------------------------------------------------
    private boolean kauth(String username, String password) {

        try {

            Process pb = new ProcessBuilder("/usr/bin/kinit", username).start(); // worked
            PrintWriter out = new PrintWriter(pb.getOutputStream());
            out.println(password);
            out.flush();
            out.close();

            pb.waitFor();

            int shellExitStatus = pb.exitValue();
            if (shellExitStatus == 0) {
                Runtime.getRuntime().exec("/usr/bin/kdestroy");
                return true;
            }

            return false;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private boolean authenticate_modified(String username, String password) {
        // get from properties file


        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        Session session = currentUser.getSession();
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if (value.equals("aValue")) {
            LOGGER.info("Retrieved the correct value! [" + value + "]");
        }

        // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                LOGGER.info("There is no user with username of " + token.getPrincipal());
                return false;
            } catch (IncorrectCredentialsException ice) {
                LOGGER.info("Password for account " + token.getPrincipal() + " was incorrect!");
                return false;
            } catch (LockedAccountException lae) {
                LOGGER.info("The account for username " + token.getPrincipal() + " is locked.  " +
                        "Please contact your administrator to unlock it.");
                return false;
            }
            // ... catch more exceptions here (maybe custom ones specific to your application?
            catch (AuthenticationException ae) {
                ae.printStackTrace(); // TODO
                //unexpected condition?  error?
                return false;
            }
        }

        //say who they are:
        //print their identifying principal (in this case, a username):
        LOGGER.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");

        return true;
    }

    // ------------------------------------------------------------------------
    public boolean authenticate(String username, String password) { //osm: see authenticate_modified()


        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        if (hostname.matches(".*local.*")) {
            System.out.println("setting usernamd and passwords");
            String[] usernames = {"test"};
            String[] passwords = {"test"};

            int i = 0;
            for (String usernameinlist : usernames) {
                if (usernameinlist.equals(username) && passwords[i].equals(password))
                    return true;
                i++;
            }
            return false;
        }

        return kauth(username, password);
    }

    // ------------------------------------------------------------------------
    public boolean fullauth(String username, String password, ModelMap model, HttpSession session, boolean itsnew, Environment env) {

        if (session == null) {
            model.addAttribute("badauth", "1");
            model.addAttribute("loginmessage", "System error");
            LOGGER.log(Level.SEVERE, "authenicate failed for username={0} due to a null session", new Object[]{username});
            return false;
        }

        int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
        session.setMaxInactiveInterval(sessiontimeout);
        session.setAttribute("loggedin", "0");
        session.setAttribute("name", "");
        session.setAttribute("username", "");
        session.setAttribute("email", "");
        session.setAttribute("isadmin", "0");

        if (username == null) {
            LOGGER.log(Level.INFO, "authenicate failed due to a null username");
            model.addAttribute("badauth", "1");
            model.addAttribute("loginmessage", "Please enter a username");
            return false;
        }

        if (password == null) {
            LOGGER.log(Level.INFO, "authenicate failed due to a null password for username={0}", new Object[]{username});
            model.addAttribute("badauth", "1");
            model.addAttribute("loginmessage", "Please enter a password");
            return false;
        }

        if (authenticate_modified(username, password)) {
            model.addAttribute("badauth", "0");
            return true;
        }

        model.addAttribute("badauth", "1");
        model.addAttribute("loginmessage", "Please try again");
        LOGGER.log(Level.INFO, "authenticate failed for username={0}", new Object[]{username});
        return false;
    }
}
