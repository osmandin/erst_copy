package submit.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.ModelMap;

import submit.entity.SubmitData;

public interface SubmissionAgreementService {
    public boolean handleSubmissionAgreementForm(SubmitData submitData, HttpServletRequest request, HttpSession session, ModelMap model);
}
