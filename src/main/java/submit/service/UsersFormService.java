package submit.service;

// $Id: UsersFormService.java,v 1.10 2016-10-04 23:38:36-04 ericholp Exp $

import submit.entity.UsersForm;

import java.util.List;
import java.util.Map;

public interface UsersFormService {
    public UsersForm create(UsersForm usersForm);
    public List<UsersForm> findAllAdmin();
    public List<UsersForm> findAllNonadmin();
    public boolean duplicate(String username, String firstname, String lastname, String email);
    public boolean duplicate(String username);
    public Map<String, Object> get(int id);
    public List<UsersForm> findByName(String name);
}
