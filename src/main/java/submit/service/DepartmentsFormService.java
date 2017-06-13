package submit.service;

import submit.entity.DepartmentsForm;

import java.util.List;

public interface DepartmentsFormService {
    public List<DepartmentsForm> findSkipUserid(int userid);

    public List<DepartmentsForm> findAllOrderByName();

    public List<DepartmentsForm> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid);

    public boolean isDuplicate(String departmentname);

    public boolean departmentAssignedToUser(int departmentid, int userid);
}
