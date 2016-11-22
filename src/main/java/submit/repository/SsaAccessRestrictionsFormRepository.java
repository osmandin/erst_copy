package submit.repository;

// $Id: SsaAccessRestrictionsFormRepository.java,v 1.9 2016-10-25 16:51:52-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import submit.entity.SsaAccessRestrictionsForm;

import java.util.List;

public interface SsaAccessRestrictionsFormRepository extends JpaRepository<SsaAccessRestrictionsForm, Integer>{
    public SsaAccessRestrictionsForm findById(int id);

    @Query(value = "SELECT a FROM SsaAccessRestrictionsForm a order by restriction asc")
    List<SsaAccessRestrictionsForm> findAllOrderByRestrictionAsc();

    @Query(value = "SELECT a FROM SsaAccessRestrictionsForm a where a.ssasForm.id=?1 order by restriction asc")
    List<SsaAccessRestrictionsForm> findAllBySsaIdOrderByRestrictionAsc(int ssaid);
}
