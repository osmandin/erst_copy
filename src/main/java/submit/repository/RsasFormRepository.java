package submit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import submit.entity.RsasForm;

public interface RsasFormRepository extends JpaRepository<RsasForm, Integer> {
    RsasForm findById(int id);

    @Query(value = "SELECT r FROM RsasForm r where r.ssasForm.id=?1 order by transferdate asc")
    List<RsasForm> findAllForSsaOrderByTransferdateAsc(int ssaid);

    List<RsasForm> findByApprovedTrueAndDeletedFalseOrderByTransferdateAsc();  // approved

    List<RsasForm> findByApprovedFalseAndDeletedFalseOrderByTransferdateAsc(); // draft
}
