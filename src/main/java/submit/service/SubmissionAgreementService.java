package submit.service;

// $Id: SubmissionAgreementService.java,v 1.4 2016-10-31 15:47:00-04 ericholp Exp $

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.ModelMap;

import submit.entity.SubmitData;

public interface SubmissionAgreementService {
    public boolean handleSubmissionAgreementForm(SubmitData submitData, HttpServletRequest request, HttpSession session, ModelMap model);
}
