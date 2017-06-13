package submit.email;

// $Id: Email.java,v 1.58 2016-11-03 13:54:34-04 ericholp Exp $

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import submit.entity.EmailSetup;
import submit.entity.LoginData;
import submit.entity.OrgInfo;
import submit.entity.RsasForm;
import submit.entity.SsasForm;
import submit.entity.SubmitAppInfo;
import submit.entity.SubmitData;
import submit.entity.SubmitRequestErrors;
import submit.entity.Defaults;
import submit.impl.CreatePdf;
import submit.impl.Utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Email {
    private final static Logger LOGGER = Logger.getLogger(Email.class.getCanonicalName());

    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;
    private EmailSetup emailsetup;
    private Utils utils;
    private Map<String, Object> model = new HashMap<String, Object>();
    private ModelMap modelmap;


    // ------------------------------------------------------------------------
    public Email(
            EmailSetup emailsetup,
            JavaMailSender mailSender,
            VelocityEngine velocityEngine,
            Environment env,
            ServletContext context,
            HttpSession session,
            ModelMap modelmap
    ) {
        this.emailsetup = emailsetup;
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
        this.modelmap = modelmap;

        utils = new Utils();

        OrgInfo orginfo;
        orginfo = new OrgInfo();
        orginfo.setEmail(env.getRequiredProperty("org.email"));
        orginfo.setPhone(env.getRequiredProperty("org.phone"));
        orginfo.setName(env.getRequiredProperty("org.name"));
        orginfo.setNamefull(env.getRequiredProperty("org.namefull"));

        SubmitAppInfo submitappinfo;
        submitappinfo = new SubmitAppInfo();
        submitappinfo.setName(env.getRequiredProperty("submit.name"));
        submitappinfo.setFrom(env.getRequiredProperty("submit.from"));
        submitappinfo.setRoot(context.getContextPath().toString());

        Defaults defaults = new Defaults();
        defaults.setRecordstitle(env.getRequiredProperty("defaults.recordstitle"));
        defaults.setCopyrightstatement(env.getRequiredProperty("defaults.copyrightstatement"));
        defaults.setRetentionschedule(env.getRequiredProperty("defaults.retentionschedule"));
        defaults.setRetentionperiod(env.getRequiredProperty("defaults.retentionperiod"));
        defaults.setArchivedescriptionstandards(env.getRequiredProperty("defaults.archivedescriptionstandards"));
        defaults.setAccessrestriction(env.getRequiredProperty("defaults.accessrestriction"));

        model.put("emailsetup", emailsetup);
        model.put("org", orginfo);
        model.put("thishost", utils.getHostname());
        model.put("submitappinfo", submitappinfo);
        model.put("defaults", defaults);
        model.put("session", session);
    }

    // ------------------------------------------------------------------------
    private void EditSsaApprovedSendToCreatorsInt(
            final SsasForm ssa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("ssa", ssa);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/EditSsaApprovedSendToCreators.vm", "UTF8", model);
                message.setText(html, true);
            }
        };

        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void ApprovedDraftRsaSendToContactsInt(
            final RsasForm rsa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("rsa", rsa);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/ApprovedDraftRsaSendToContacts.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void SubmitRequestSendToUserInt(
            final SubmitData submitdata,
            final SubmitRequestErrors errors
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getTo());
                message.setSubject(emailsetup.getSubject());

                submitdata.setDate(String.format("%1$tB %1$td, %1$tY", Calendar.getInstance()));

                model.put("submitdata", submitdata);
                model.put("errors", errors);

                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/SubmitRequestSendToUser.vm", "UTF8", model);
                message.setText(text, true);

                String htmlstring = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/Request.vm", "UTF8", model);

                CreatePdf cpdf = new CreatePdf();
                byte[] databa = cpdf.createpdffromstringandreturnbytearray(htmlstring);
                message.addAttachment("request.pdf", new ByteArrayResource(databa), "application/pdf");
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void SubmitRequestSendToStaffInt(
            final SubmitData submitdata,
            final SubmitRequestErrors errors
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());


                String date = String.format("%1$tB %1$td, %1$tY", Calendar.getInstance());
                submitdata.setDate(date);

                model.put("submitdata", submitdata);
                model.put("errors", errors);

                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/SubmitRequestSendToStaff.vm", "UTF8", model);
                message.setText(text, true);

                String emailhtml = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/SubmitRequestSendToUser.vm", "UTF8", model);

                byte[] emailasbytearray = emailhtml.getBytes();

                message.addAttachment("useremail.html", new ByteArrayResource(emailasbytearray), "text/html");

                String htmlstring = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/Request.vm", "UTF8", model);

                CreatePdf cpdf = new CreatePdf();
                byte[] databa = cpdf.createpdffromstringandreturnbytearray(htmlstring);
                message.addAttachment("request.pdf", new ByteArrayResource(databa), "application/pdf");
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void LoginInt(
            final LoginData loginData
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException, AddressException {

                model.put("loginData", loginData);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/Login.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void UploadCompleteSendToStaffInt(
            final RsasForm rsa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("rsa", rsa);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());


                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/UploadCompleteSendToStaff.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void UploadCompleteSendToUserInt(
            final RsasForm rsa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("rsa", rsa);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getTo());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/UploadCompleteSendToUser.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void DeleteDraftRsasSendToStaffInt(
            final List<RsasForm> rsas
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("rsas", rsas);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getToarray());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/DeleteDraftRsasSendToStaff.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void NotifyUserInt() throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getTo());
                message.setSubject(emailsetup.getSubject());

                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/NotifyUser.vm", "UTF8", model);
                message.setText(text, false);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void DeleteDraftRsaSendToStaffInt(
            final RsasForm rsa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                model.put("rsa", rsa);

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getTo());
                message.setSubject(emailsetup.getSubject());

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/DeleteDraftRsaSendToStaff.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    private void EditDraftRsaSendToUserInt(
            final RsasForm rsa
    ) throws MailParseException {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws MessagingException {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setFrom(emailsetup.getFrom());
                message.setTo(emailsetup.getTo());
                message.setSubject(emailsetup.getSubject());

                model.put("rsa", rsa);

                String html = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/EditDraftRsaSendToUser.vm", "UTF8", model);
                message.setText(html, true);
            }
        };
        mailSender.send(preparator);
    }

    // ------------------------------------------------------------------------
    public boolean InvalidAddress(MailSendException ex) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        String stackTrace = writer.toString();

        String[] exlines = stackTrace.split("\n");
        for (String line : exlines) {
            if (line.matches(".*Invalid Addresses.*")) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    public boolean EditSsaApprovedSendToCreators(
            SsasForm ssa
    ) {
        try {
            EditSsaApprovedSendToCreatorsInt(ssa);
            modelmap.addAttribute("failed", "0");
            modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
            return true;
        } catch (MailSendException ex) {

            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: one or more invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
                modelmap.addAttribute("invalidaddresses", "1");
                modelmap.addAttribute("failed", "0");
            } else {
                LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: {0}", new Object[]{ex});
                modelmap.addAttribute("failed", "1");
            }

            modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
        } catch (MailParseException ex) {
            modelmap.addAttribute("failed", "1");
            LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            modelmap.addAttribute("failed", "1");
            LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            modelmap.addAttribute("failed", "1");
            LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: {0}", new Object[]{ex});
        } catch (Exception ex) {
            modelmap.addAttribute("failed", "1");
            LOGGER.log(Level.SEVERE, "EditSsaApprovedSendToCreators: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean ApprovedDraftRsaSendToContacts(
            RsasForm rsa
    ) {
        try {
            ApprovedDraftRsaSendToContactsInt(rsa);

            modelmap.addAttribute("failed", "0");
            modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: one or more invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
                modelmap.addAttribute("invalidaddresses", "1");
                modelmap.addAttribute("failed", "0");
            } else {
                LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: {0}", new Object[]{ex});
                modelmap.addAttribute("failed", "1");
            }

            modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "ApprovedDraftRsaSendToContacts: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean SubmitRequestSendToUser(
            SubmitData submitdata,
            SubmitRequestErrors errors
    ) {
        try {
            SubmitRequestSendToUserInt(
                    submitdata,
                    errors
            );
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToUser: one or more invalid addresses for {0}", new Object[]{emailsetup.getTo()});
            } else {
                LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToUser: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToStaff: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean SubmitRequestSendToStaff(
            SubmitData submitdata,
            SubmitRequestErrors errors
    ) {
        try {
            SubmitRequestSendToStaffInt(
                    submitdata,
                    errors
            );
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToStaff: one or more invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
            } else {
                LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToStaff: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "sendSubmitEmail: SubmitRequestSendToStaff: {0}", new Object[]{ex});
        }
        return false;
    }


    // ------------------------------------------------------------------------
    public boolean DeleteDraftRsaSendToStaff(
            RsasForm rsa
    ) {
        modelmap.addAttribute("failed", "0");
        modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
        try {
            DeleteDraftRsaSendToStaffInt(rsa);
            return true;
        } catch (MailSendException ex) {

            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: one or more invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
                modelmap.addAttribute("invalidaddresses", "1");
                modelmap.addAttribute("failed", "0");
            } else {
                LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: {0}", new Object[]{ex});
                modelmap.addAttribute("failed", "1");
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsaSendToStaff: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean NotifyUser() {
        try {
            NotifyUserInt();
            modelmap.addAttribute("emailsent", "1");
            modelmap.addAttribute("failed", "0");
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "NotifyUser: one or more invalid addesses for {0}", new Object[]{emailsetup.getTo()});
                modelmap.addAttribute("invalidaddresses", "1");
            } else {
                LOGGER.log(Level.SEVERE, "NotifyUser: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "NotifyUser: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "NotifyUser: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "NotifyUser: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "NotifyUser: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean EditDraftRsaSendToUser(
            RsasForm rsa
    ) {
        try {
            EditDraftRsaSendToUserInt(rsa);
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: invalid addresses for {0}", new Object[]{emailsetup.getTo()});
            } else {
                LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "EditDraftRsaSendToUser: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean Login(
            LoginData logindata
    ) {
        try {
            LoginInt(logindata);
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "Login: invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
            } else {
                LOGGER.log(Level.SEVERE, "Login: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "Login: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Login: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "Login: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Login: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean UploadCompleteSendToStaff(
            RsasForm rsa
    ) {

        try {
            UploadCompleteSendToStaffInt(rsa);
            modelmap.addAttribute("staffemailsent", true);
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
            } else {
                LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            modelmap.addAttribute("staffemailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            modelmap.addAttribute("staffemailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            modelmap.addAttribute("staffemailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: {0}", new Object[]{ex});
        } catch (Exception ex) {
            modelmap.addAttribute("staffemailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToStaff: {0}", new Object[]{ex});
        }
        // return false;
        LOGGER.log(Level.SEVERE, "RETURNING TRUE ANYWAY");
        modelmap.addAttribute("staffemailsent", true);

        return true; //FIXME osman changed this
    }

    // ------------------------------------------------------------------------
    public boolean UploadCompleteSendToUser(
            RsasForm rsa
    ) {

        try {
            UploadCompleteSendToUserInt(rsa);
            modelmap.addAttribute("useremailsent", true);
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
            } else {
                LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: {0}", new Object[]{ex});
            }
            modelmap.addAttribute("useremailsent", false);
        } catch (MailParseException ex) {
            modelmap.addAttribute("useremailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            modelmap.addAttribute("useremailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            modelmap.addAttribute("useremailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: {0}", new Object[]{ex});
        } catch (Exception ex) {
            modelmap.addAttribute("useremailsent", false);
            LOGGER.log(Level.SEVERE, "UploadCompleteSendToUser: {0}", new Object[]{ex});
        }
        return false;
    }

    // ------------------------------------------------------------------------
    public boolean DeleteDraftRsasSendToStaff(
            List<RsasForm> rsas
    ) {
        modelmap.addAttribute("emailrecips", emailsetup.getToarrayasstr());
        modelmap.addAttribute("failed", "0");
        modelmap.addAttribute("invalidaddresses", "0");
        try {
            DeleteDraftRsasSendToStaffInt(rsas);
            return true;
        } catch (MailSendException ex) {
            if (InvalidAddress(ex)) {
                modelmap.addAttribute("invalidaddresses", "1");
                modelmap.addAttribute("failed", "0");
                LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: one or more invalid addresses for {0}", new Object[]{emailsetup.getToarrayasstr()});
            } else {
                LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: {0}", new Object[]{ex});
            }
        } catch (MailParseException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: {0}", new Object[]{ex});
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: {0}", new Object[]{ex});
        } catch (MailPreparationException ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: {0}", new Object[]{ex});
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "DeleteDraftRsasSendToStaff: {0}", new Object[]{ex});
        }
        modelmap.addAttribute("failed", "1");
        return false;
    }

    // ------------------------------------------------------------------------
    public void sendSubmitEmail(
            EmailSetup staffemailsetup,
            SubmitData submitdata,
            SubmitRequestErrors errors,
            HttpServletRequest request
    ) {
        boolean useremailsucceeded = SubmitRequestSendToUser(submitdata, errors);
        request.setAttribute("useremailsent", useremailsucceeded);

        emailsetup = staffemailsetup;
        errors.setUseremailfailed(!useremailsucceeded);
        SubmitRequestSendToStaff(submitdata, errors);
    }
}

