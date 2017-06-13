package submit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import submit.entity.UsersForm;

public interface UsersFormRepository extends JpaRepository<UsersForm, Integer> {
    UsersForm findById(int id);

    List<UsersForm> findByIsadminFalse();

    List<UsersForm> findByIsadminFalseOrderByLastnameAscFirstnameAsc();

    List<UsersForm> findByIsadminTrueOrderByLastnameAscFirstnameAsc();

    List<UsersForm> findByUsernameAndFirstnameAndLastnameAndEmail(String username, String firstname, String lastname, String email);

    List<UsersForm> findByUsername(String username);
}
