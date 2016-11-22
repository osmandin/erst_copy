package submit.service;

// $Id: DepartmentsFormService.java,v 1.9 2016-10-31 15:07:03-04 ericholp Exp $

import submit.entity.DepartmentsForm;

import java.util.List;

public interface DepartmentsFormService {
    public List<DepartmentsForm> findSkipUserid(int userid);
    public List<DepartmentsForm> findAllOrderByName();
    public List<DepartmentsForm> findAllNotAssociatedWithOtherSsaOrderByName(int thisssaid);
    public boolean isDuplicate(String departmentname);
    public boolean departmentAssignedToUser(int departmentid, int userid);
}
