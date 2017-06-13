package submit.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import submit.captcha.VerifyRecaptcha;
import submit.email.Email;
import submit.entity.DepartmentsForm;
import submit.entity.EmailSetup;
import submit.entity.FileData;
import submit.entity.LoginData;
import submit.entity.OrgInfo;
import submit.entity.RsaFileDataForm;
import submit.entity.RsasForm;
import submit.entity.SsasForm;
import submit.entity.SubmitAppInfo;
import submit.entity.SubmitData;
import submit.entity.UsersForm;
import submit.impl.Auth;
import submit.impl.CreatePdf;
import submit.impl.Format;
import submit.impl.Utils;
import submit.impl.MyFileUtils;
import submit.repository.RsaFileDataFormRepository;
import submit.repository.RsasFormRepository;
import submit.repository.SsasFormRepository;
import submit.repository.UsersFormRepository;
import submit.service.DepartmentsFormService;

@Controller
public class UserPages {
    private final static Logger LOGGER = Logger.getLogger(UserPages.class.getCanonicalName());

    private boolean isadmin = false;
    private String email = "";

    @Resource
    private Environment env;

    private static VelocityEngine velocityEngine;

    @Autowired
    public void setVelocityEngine(VelocityEngine ve) {
        velocityEngine = ve;
    }

    private static JavaMailSenderImpl sender;

    @Autowired
    public void setSender(JavaMailSenderImpl sender) {
        UserPages.sender = sender;
    }

    @Autowired
    ServletContext context;

    @Autowired
    private UsersFormRepository userrepo;

    @Autowired
    DepartmentsFormService departmentservice;

    @Autowired
    private SsasFormRepository ssarepo;

    @Autowired
    private RsasFormRepository rsarepo;

    @Autowired
    private RsaFileDataFormRepository filedatarepo;

    // ------------------------------------------------------------------------
    @RequestMapping({"/", "/Home"})
    public String Home(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "Home");
        model.addAttribute("banner", (System.currentTimeMillis() / 1000l) % 6 + 1);
        model.addAttribute("page", "Home");
        session.setAttribute("captchacorrect", null);
        return "Home";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/Logout")
    public String Logout(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "Logout");

        session.setAttribute("loggedin", "0");
        session.setAttribute("isadmin", "0");
        session.setAttribute("name", "");
        session.setAttribute("username", "");
        session.setAttribute("email", "");

        return "Home";
    }

    // ------------------------------------------------------------------------
    String authorized(String username) {
        List<UsersForm> ufs = userrepo.findByUsername(username);
        if (ufs.size() == 1) {
            isadmin = ufs.get(0).isIsadmin();
            email = ufs.get(0).getEmail();
            return ufs.get(0).getFirstname() + " " + ufs.get(0).getLastname();
        } else if (ufs.size() > 1) {
            LOGGER.log(Level.INFO, "too many user accounts for user={0} size={1}", new Object[]{username, ufs.size()});
        }
        return null;
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/Auth", method = RequestMethod.GET)
    public String AuthGet(
            final LoginData logindata,
            ModelMap model,
            HttpServletRequest request,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "AuthGet");

        model.addAttribute("page", "Auth");

        String staffemail = env.getRequiredProperty("org.email");
        model.addAttribute("staff_email", staffemail);

        if (session.isNew()) {
            String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
            Utils utils = new Utils();
            if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
                LOGGER.log(Level.SEVERE, "Not an accepted address");
                return "Home";
            }
        }

        int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
        session.setMaxInactiveInterval(sessiontimeout);

        model.addAttribute("loggedin", 0);
        model.addAttribute("badauth", 0);
        return "Auth";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/Auth", method = RequestMethod.POST)
    public String Auth(
            final LoginData logindata,
            ModelMap model,
            HttpServletRequest request,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "Auth Post");

        model.addAttribute("page", "Auth");

        String staffemail = env.getRequiredProperty("org.email");
        model.addAttribute("staff_email", staffemail);

        boolean itsnew = false;
        if (session.isNew()) {
            itsnew = true;

            String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
            Utils utils = new Utils();
            if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
                LOGGER.log(Level.SEVERE, "Not an accepted address");
                return "Home";
            }
        }


        int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
        session.setMaxInactiveInterval(sessiontimeout);

        if (session.getAttribute("name") != null) {
            model.addAttribute("name", session.getAttribute("name").toString());
        }

        String username = logindata.getUsername();
        String password = logindata.getPassword();

        Auth auth = new Auth();
        boolean loggedin = auth.fullauth(username, password, model, session, itsnew, env);

        if (!loggedin) {
            LOGGER.log(Level.INFO, "login failed for username={0}", new Object[]{username});
            return "Auth";
        }

        String name = authorized(username);  // osm: really bad code here, it sets
        LOGGER.log(Level.INFO, "Looked up acct userName:" + name);
        //name = "test"; //FIXME


        if (name == null) { // not authorized
            LOGGER.info("not authorized");
            model.addAttribute("badauth", "1");
            model.addAttribute("loginmessage", "Not authorized");
            return "Auth";
        }

        session.setAttribute("loggedin", "1");
        session.setAttribute("name", name);
        session.setAttribute("username", username);
        session.setAttribute("email", email);

        session.setAttribute("isadmin", "0");

        if (isadmin) {
            LOGGER.info("The user is an admin");
            session.setAttribute("isadmin", "1");
        } else {

            LOGGER.info("The user is not an admin");
        }

        if (env.getRequiredProperty("sendloginemails").equals("true")) {

            String[] ignoreusernamesarray = env.getRequiredProperty("loginignoresusernames").split("[, ]+");
            boolean sendemail = true;
            for (String ignoreusername : ignoreusernamesarray) {
                if (ignoreusername.equals(username)) {
                    sendemail = false;
                    break;
                }
            }

            // FIXME: changed by osm
            sendemail = false;


            if (sendemail) {
                LOGGER.log(Level.INFO, "sending email for username={0}", new Object[]{username});

                String[] recipts = env.getRequiredProperty("loginemailrecipts").split("[, ]+");

                EmailSetup emailsetup = new EmailSetup();
                emailsetup.setFrom("no-reply");
                emailsetup.setSubject("Login to " + context.getContextPath().substring(1)); // strip off leading slash
                emailsetup.setToarray(recipts);

                Utils utils = new Utils();
                logindata.setHostname(utils.getHostname());

                Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
                email.Login(logindata);

            }
        }

        model.addAttribute("loggedin", 1);
        model.addAttribute("badauth", 0);

        LOGGER.log(Level.INFO, "going to home screen for user={0}", new Object[]{username});


        return "Home";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/SubmitRecords")
    public String SubmitRecords(
            ModelMap model,
            HttpServletRequest request,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "SubmitRecords");

        model.addAttribute("page", "SubmitRecords");

        if (session.isNew()) {
            String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
            Utils utils = new Utils();
            if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
                LOGGER.log(Level.SEVERE, "Not an accepted address");
                model.addAttribute("displaysubmitlink", 0);
                //return "Home"; //FIXME OSMAN REMVOED
            }
        }

        if (session.getAttribute("loggedin") == null || session.getAttribute("loggedin").toString().equals("0")) {
            LoginData logindata = new LoginData();
            model.addAttribute("loginData", logindata);
            return "Auth";
        }

        String username = session.getAttribute("username").toString();
        if (username == null || username.equals("")) {
            LOGGER.log(Level.SEVERE, "null or blank username");
            return "Home";
        }

        int sessiontimeout = Integer.parseInt(env.getRequiredProperty("session.timeout"));
        session.setMaxInactiveInterval(sessiontimeout);

        model.addAttribute("departments", 0);


        String isadmin_str = (String) session.getAttribute("isadmin");
        boolean isadmin = false;
        if (isadmin_str != null && isadmin_str.equals("1")) {
            isadmin = true;
        }

        try {
            if (isadmin) {
                List<SsasForm> ssas = ssarepo.findAllEnabledDepartments();
                if (ssas != null && !ssas.isEmpty() && ssas.size() != 0) {
                    model.addAttribute("departments", 1);
                    model.addAttribute("ssaForms", ssas);
                }
                return "SubmitRecords";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<UsersForm> users = userrepo.findByUsername(username);
        if (users.size() != 1) {
            return "SubmitRecords";
        }

        UsersForm user = users.get(0);

        List<SsasForm> usersssas = new ArrayList<SsasForm>();

        List<DepartmentsForm> departments = user.getDepartmentsForms();
        for (DepartmentsForm df : departments) {
            int deptid = df.getId();
            List<SsasForm> ssas = ssarepo.findAll();
            for (SsasForm ssa : ssas) {
                DepartmentsForm ssadf = ssa.getDepartmentForm();
                if (ssadf.getId() == deptid) {
                    usersssas.add(ssa);
                }
            }
        }

        if (usersssas != null && usersssas.size() > 0) {
            model.addAttribute("departments", 1);
            model.addAttribute("ssaForms", usersssas);
        }

        return "SubmitRecords";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/RecordsSubmissionForm", method = RequestMethod.POST)
    public String RecordsSubmissionForm(
            ModelMap model,
            @RequestParam(value = "ssaid", required = false) int ssaid,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "RecordsSubmissionForm Post");

        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }

        if (ssaid <= 0 && session.getAttribute("ssaid") != null && !session.getAttribute("ssaid").equals("")) {
            ssaid = Integer.parseInt(session.getAttribute("ssaid").toString());
        }

        session.setAttribute("ssaid", ssaid);

        if (ssaid != 0) {
            LOGGER.log(Level.INFO, "ssaid={0}", new Object[]{ssaid});
            SsasForm ssaform = ssarepo.findById(ssaid);

            model.addAttribute("ssa", ssaform);
        } else {
            LOGGER.log(Level.INFO, "ssaid is zero!!!");
            SsasForm ssasForm = new SsasForm();
            model.addAttribute("ssasForm", ssasForm);
        }

        return "RecordsSubmissionForm";
    }

    // ------------------------------------------------------------------------
    @RequestMapping("/UploadFiles")
    public String UploadFiles(
            ModelMap model,
            @RequestParam(value = "generalRecordsDescription", required = false) String generalRecordsDescription,
            @RequestParam(value = "startyear", required = false) String startyear,
            @RequestParam(value = "endyear", required = false) String endyear,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadFiles");

        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }

        Format format = new Format();
        String avail = format.showavailbytes(env.getRequiredProperty("dropoff.dir"));
        model.addAttribute("avail_bytes", avail);

        session.setAttribute("generalRecordsDescription", generalRecordsDescription);
        session.setAttribute("startyear", startyear);
        session.setAttribute("endyear", endyear);

        model.addAttribute("totalmax", env.getRequiredProperty("js.totalmax"));
        model.addAttribute("peruploadmax", env.getRequiredProperty("js.peruploadmax"));

        return "UploadFiles";
    }

    // ------------------------------------------------------------------------
    @ResponseBody
    @RequestMapping("/CheckSpace")
    public String CheckSpace(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "CheckSpace");

        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "";
        }

        File f = new File(env.getRequiredProperty("dropoff.dir"));
        Long bytes = f.getUsableSpace();
        String strbytes = Long.toString(bytes);
        LOGGER.log(Level.INFO, "available bytes: {0}", new Object[]{strbytes});

        return strbytes;
        //return "338610"; // 338614 Screen Shot 2016-11-03 at 10.30.25 AM.png
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.GET)
    public String UploadComplete(
            ModelMap model,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadComplete GET");

        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }

        String ssaid = (String) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);

        return "UploadComplete";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/UploadComplete", method = RequestMethod.POST)
    public String UploadComplete(
            @RequestParam("file") MultipartFile[] files,
            ModelMap model,
            HttpServletRequest request,
            @RequestParam(value = "filedata", required = false) String myfiledatastring,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "UploadComplete POST");

        Utils utils = new Utils();
        if (!utils.setupAuthdHandler(model, session, env)) {
            return "Home";
        }

        int ssaid = (Integer) session.getAttribute("ssaid");
        model.addAttribute("ssaid", ssaid);


        String description = (String) session.getAttribute("generalRecordsDescription");
        String startyear = (String) session.getAttribute("startyear");
        String endyear = (String) session.getAttribute("endyear");
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        String useremail = (String) session.getAttribute("email");

        if (username == null || username.equals("")) {
            LOGGER.log(Level.SEVERE, "username is blank");
            model.addAttribute("nooffices", 1);
            return "Home";
        }

        if (endyear == null || endyear.equals("") || endyear.matches("^\\s*$")) {
            endyear = startyear;
        }

        String sqldate = String.format("%1$tY-%1$tm-%1$td", Calendar.getInstance());

        RsasForm rsa = new RsasForm();
        rsa.setStartyear(startyear);
        rsa.setEndyear(endyear);
        rsa.setDescription(description);
        rsa.setApproved(false);
        rsa.setCreatedby(name); // name here... not username... change?
        rsa.setTransferdate(sqldate);

        SsasForm ssa = ssarepo.findById(ssaid);
        if (ssa == null || ssaid == 0) {
            LOGGER.log(Level.SEVERE, "ssa is null or ssaid is zero: ssaid={0}", new Object[]{ssaid});
        } else {
            rsa.setSsasForm(ssa);
        }

        rsa = rsarepo.save(rsa);

        model.addAttribute("rsaid", rsa.getId());

        String dropoffdirfull = env.getRequiredProperty("dropoff.dir") + "/" + Integer.toString(rsa.getId());
        File dir = new File(dropoffdirfull);
        boolean successful = dir.mkdir();
        if (!successful) {
            LOGGER.log(Level.SEVERE, "dir={0} NOT created", new Object[]{dropoffdirfull});
        }

        Format format = new Format();
        List<FileData> fileinfodata = format.parseFileInfo(myfiledatastring);

        model.addAttribute("filedata", fileinfodata);
        model.addAttribute("totalfilesize", format.getTotalfilesizestr());

        List<RsaFileDataForm> rfds = new ArrayList<RsaFileDataForm>();
        for (FileData filedetails : fileinfodata) {
            RsaFileDataForm fd = new RsaFileDataForm();
            fd.setName(filedetails.getName());
            fd.setSize(filedetails.getSize());
            fd.setNicesize(format.displayBytes(filedetails.getSize()));
            fd.setLastmoddatetime(filedetails.getLastmoddatetime());
            fd.setRsasForm(rsa);

            fd = filedatarepo.save(fd);
            rfds.add(fd);
        }
        rsa.setRsaFileDataForms(rfds);

        rsa.setExtent(format.getTotalfilesize());
        rsa.setExtentstr(format.getTotalfilesizestr());

        rsa = rsarepo.save(rsa);

        if (files == null) {
            LOGGER.log(Level.SEVERE, "files null");
        } else {
            rfds = new ArrayList<RsaFileDataForm>();
            MyFileUtils fileutils = new MyFileUtils();
            List<FileData> uploadfileinfo = fileutils.uploadFiles(files, dropoffdirfull, fileinfodata);
            for (FileData fileinfo : uploadfileinfo) {
                if (fileinfo.getName().equals("")) { // ignore cases where filename is empty... happenes when file tag is created in page but not populated
                    LOGGER.log(Level.INFO, "for rsa={0} filename is blank as happens when file tag used but not populated", new Object[]{rsa.getId()});
                } else {
                    List<RsaFileDataForm> fds = filedatarepo.findBasedOnIdAndFilename(rsa.getId(), fileinfo.getName());
                    if (fds.size() != 1) {
                        LOGGER.log(Level.SEVERE, "Error: incorrect number of matches ({0}) for rsa={1} filename={2} myfiledatastring={3}", new Object[]{fds.size(), rsa.getId(), fileinfo.getName(), myfiledatastring});
                        continue;
                    }

                    if (!fileinfo.getSetmoddatetimestatus().equals("success")) {
                        LOGGER.log(Level.SEVERE, "Error: setmoddatetimestatus={0} for rsa={1} filename={2}", new Object[]{fileinfo.getSetmoddatetimestatus(), rsa.getId(), fileinfo.getName()});
                    }
                    // update with extra info
                    RsaFileDataForm fd = fds.get(0);
                    fd.setStatus(fileinfo.getStatus());
                    fd.setSize(fileinfo.getSize());
                    fd.setNicesize(format.displayBytes((long) fileinfo.getSize()));
                    fd.setStatus(fileinfo.getStatus());
                    fd = filedatarepo.save(fd);
                    rfds.add(fd);
                }
            }

            rsa.setRsaFileDataForms(rfds);
            rsa = rsarepo.save(rsa);
        }

        String isadmin_str = (String) session.getAttribute("isadmin");
        boolean isadmin = false;
        if (isadmin_str != null && isadmin_str.equals("1")) {
            isadmin = true;
        }

        List<SsasForm> ssas = null;
        if (isadmin) {
            ssarepo.find
            ssas = ssarepo.findAllEnabledDepartments();
        } else {
            ssas = ssarepo.findAllActiveApprovedEnabledDepartmentsForUsername(username);
        }

        if (ssas == null || ssas.isEmpty()) {
            LOGGER.log(Level.SEVERE, "ssa null or empty for username={0} isadmin={1}", new Object[]{username, isadmin});
            model.addAttribute("departments", 0);
            return "Home";
        }

        model.addAttribute("departments", 1);
        model.addAttribute("ssaForms", ssas);

        EmailSetup emailsetup = new EmailSetup();

        emailsetup.setFrom(env.getRequiredProperty("submit.from"));
        emailsetup.setSubject("New Records Transfer Request: " + rsa.getId());

        String[] recipts = env.getRequiredProperty("org.email").split(",");

        emailsetup.setToarray(recipts);

        Email email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
        email.UploadCompleteSendToStaff(rsa);

        emailsetup = new EmailSetup();

        emailsetup.setFrom(env.getRequiredProperty("submit.from"));
        emailsetup.setSubject("Draft Transfer Request sent");

        String useremailaddress = "\"" + name.trim() + "\" <" + useremail.trim() + ">";

        emailsetup.setTo(useremailaddress);

        email = new Email(emailsetup, sender, velocityEngine, env, context, session, model);
        email.UploadCompleteSendToUser(rsa);

        return "UploadComplete";
    }

    // ------------------------------------------------------------------------
    @RequestMapping(value = "/VerifySave", method = RequestMethod.POST)
    public void VerifySave(
            ModelMap model,
            SubmitData submitData,
            @RequestParam(value = "g-recaptcha-response", required = false) String response,
            HttpServletRequest request,
            HttpServletResponse httpresponse,
            HttpSession session
    ) {
        LOGGER.log(Level.INFO, "VerifySave Post");

        Utils utils = new Utils();

        if (session.getAttribute("captchacorrect") != null && !session.getAttribute("captchacorrect").toString().equals("1")) {
            LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");
            utils.redirectToRoot(context, httpresponse);
            return;
        }

        String acceptedaddressmatch = env.getRequiredProperty("acceptedaddressmatch");
        if (!utils.isAcceptedAddress(request, acceptedaddressmatch)) {
            utils.redirectToRoot(context, httpresponse);
            LOGGER.log(Level.SEVERE, "Not an accepted address");
            return;
        }

        model.addAttribute("captchacorrect", "0");
        session.setAttribute("captchacorrect", "0");
        session.setAttribute("captchaincorrect", "0");

        String staffemail = env.getRequiredProperty("org.email");
        model.addAttribute("staff_email", staffemail);

        String publickey = env.getRequiredProperty("publiccaptchakey");
        model.addAttribute("publickey", publickey);

        model.addAttribute("submitrecap", "1");

        if (response == null) {
            LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");
            session.setAttribute("captchaincorrect", "1");
            utils.redirectToRoot(context, httpresponse);
            return;
        }

        String privatekey = env.getRequiredProperty("privatecaptchakey");

        boolean correct = VerifyRecaptcha.verify(response, privatekey);

        if (correct) {
            model.addAttribute("cpatchacorrect", "1");
            session.setAttribute("captchacorrect", "1");

            String date = String.format("%1$tB %1$td, %1$tY", Calendar.getInstance());
            submitData.setDate(date);

            OrgInfo orginfo = new OrgInfo();
            orginfo.setEmail(env.getRequiredProperty("org.email"));
            orginfo.setPhone(env.getRequiredProperty("org.phone"));
            orginfo.setName(env.getRequiredProperty("org.name"));
            orginfo.setNamefull(env.getRequiredProperty("org.namefull"));

            SubmitAppInfo submitappinfo = new SubmitAppInfo();
            submitappinfo.setName(env.getRequiredProperty("submit.name"));
            submitappinfo.setFrom(env.getRequiredProperty("submit.from"));
            submitappinfo.setRoot(context.getContextPath());

            Map<String, Object> datamodel = new HashMap<String, Object>();
            datamodel.put("info", submitData);
            datamodel.put("org", orginfo);
            datamodel.put("submit", submitappinfo);
            datamodel.put("session", session);

            String htmldata = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/Request.vm", "UTF8", datamodel);

            CreatePdf cp = new CreatePdf();
            cp.downloadpdf(httpresponse, htmldata);

            return;
        }

        LOGGER.log(Level.INFO, "VerifySave Post: Captcha entry incorrect");

        session.setAttribute("captchaincorrect", "1");

        model.addAttribute("submitData", submitData);

        utils.redirectToRoot(context, httpresponse);
        return;
    }
}
