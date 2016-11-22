package submit.repository;

import java.util.List;

// $Id: OnlineSubmissionRequestFormRepository.java,v 1.8 2016-10-25 16:50:34-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;

import submit.entity.OnlineSubmissionRequestForm;

public interface OnlineSubmissionRequestFormRepository extends JpaRepository<OnlineSubmissionRequestForm, Integer>{
    public List<OnlineSubmissionRequestForm> findByDepartmentAndAddressAndNameAndEmailAndPhoneAndDepartmentheadAndSignature(String department, String address, String name, String email, String phone, String departmenthead, String signature);
    public OnlineSubmissionRequestForm findById(int id);
    public List<OnlineSubmissionRequestForm> findBySsaid(int ssaid);
}
