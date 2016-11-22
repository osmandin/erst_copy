package submit.service;

// $Id: ApprovedRsasFormService.java,v 1.5 2016-10-31 16:25:28-04 ericholp Exp $

import javax.servlet.http.HttpSession;

import submit.entity.RsasForm;

public interface ApprovedRsasFormService {
    public String findAllApprovedTransfersCSV();
    public void recordDeletedRsa(RsasForm rsa, HttpSession session);
}
