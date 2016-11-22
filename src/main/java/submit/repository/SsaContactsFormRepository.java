package submit.repository;

// $Id: SsaContactsFormRepository.java,v 1.7 2016-10-04 23:36:04-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import submit.entity.SsaContactsForm;

public interface SsaContactsFormRepository extends JpaRepository<SsaContactsForm, Integer>{
    public SsaContactsForm findById(int id);

    @Query(value = "SELECT c FROM SsaContactsForm c where c.ssasForm.id=?1 order by name asc")
    List<SsaContactsForm> findAllBySsaIdOrderByNameAsc(int ssaid);
}
