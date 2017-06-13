package submit.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import submit.entity.UsersForm;
import submit.repository.UsersFormRepository;

import submit.entity.DepartmentsForm;
import submit.repository.DepartmentsFormRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UsersFormServiceImpl implements UsersFormService {
    private final static Logger LOGGER = Logger.getLogger(UsersFormServiceImpl.class.getCanonicalName());

    @Resource
    private UsersFormRepository usersFormRepository;

    @Resource
    private DepartmentsFormRepository departmentsFormRepository;

    @Transactional
    public UsersForm create(UsersForm usersForm) {
        return usersFormRepository.save(usersForm);
    }

    @Transactional
    public List<UsersForm> findAllAdmin() {
        List<UsersForm> adminForms = usersFormRepository.findByIsadminTrueOrderByLastnameAscFirstnameAsc();
        return adminForms;
    }

    @Transactional
    public List<UsersForm> findAllNonadmin() {
        List<UsersForm> nonadminForms = usersFormRepository.findByIsadminFalseOrderByLastnameAscFirstnameAsc();
        return nonadminForms;
    }

    @Transactional
    public boolean duplicate(String username, String firstname, String lastname, String email) {
        List<UsersForm> ufs = usersFormRepository.findByUsernameAndFirstnameAndLastnameAndEmail(username, firstname, lastname, email);
        return ufs.size() > 0;
    }

    @Transactional
    public boolean duplicate(String username) {
        List<UsersForm> ufs = usersFormRepository.findByUsername(username);
        return ufs.size() > 0;
    }


    @Transactional
    public Map<String, Object> get(int id) {
        Map<String, Object> info = new HashMap<String, Object>();
        UsersForm uf = usersFormRepository.findById(id);
        if (uf == null) {
            return null;
        }
        info.put("id", Integer.toString(uf.getId()));
        info.put("username", uf.getUsername());
        info.put("last_name", uf.getLastname());
        info.put("first_name", uf.getFirstname());
        info.put("email", uf.getEmail());
        info.put("userenabled", uf.isEnabled() ? "1" : "0");
        info.put("is_admin", uf.isIsadmin() ? "1" : "0");
        return info;
    }

    @Transactional
    public List<UsersForm> findByName(String name) {
        List<UsersForm> newus = new ArrayList<UsersForm>();
        if (name == null) {
            return newus;
        }
        // assume name is "firstname lastname"
        String[] parts = name.split(" +");
        List<UsersForm> us = usersFormRepository.findAll();
        if (parts.length != 2) {
            return newus;
        }
        for (UsersForm u : us) {
            if (parts[0].equals(u.getFirstname()) && parts[0].equals(u.getFirstname())) {
                newus.add(u);
            }
        }
        return newus;
    }

}

