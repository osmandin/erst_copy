package submit.service;

import javax.servlet.http.HttpSession;

import submit.entity.RsasForm;

public interface ApprovedRsasFormService {
    public String findAllApprovedTransfersCSV();
    public void recordDeletedRsa(RsasForm rsa, HttpSession session);
}
