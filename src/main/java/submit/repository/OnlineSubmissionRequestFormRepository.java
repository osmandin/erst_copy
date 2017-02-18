package submit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import submit.entity.OnlineSubmissionRequestForm;

public interface OnlineSubmissionRequestFormRepository extends JpaRepository<OnlineSubmissionRequestForm, Integer>{
    public List<OnlineSubmissionRequestForm> findByDepartmentAndAddressAndNameAndEmailAndPhoneAndDepartmentheadAndSignature(String department, String address, String name, String email, String phone, String departmenthead, String signature);
    public OnlineSubmissionRequestForm findById(int id);
    public List<OnlineSubmissionRequestForm> findBySsaid(int ssaid);
}
