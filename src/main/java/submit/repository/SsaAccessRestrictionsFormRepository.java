package submit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import submit.entity.SsaAccessRestrictionsForm;

import java.util.List;

public interface SsaAccessRestrictionsFormRepository extends JpaRepository<SsaAccessRestrictionsForm, Integer> {
    public SsaAccessRestrictionsForm findById(int id);

    @Query(value = "SELECT a FROM SsaAccessRestrictionsForm a order by restriction asc")
    List<SsaAccessRestrictionsForm> findAllOrderByRestrictionAsc();

    @Query(value = "SELECT a FROM SsaAccessRestrictionsForm a where a.ssasForm.id=?1 order by restriction asc")
    List<SsaAccessRestrictionsForm> findAllBySsaIdOrderByRestrictionAsc(int ssaid);
}
