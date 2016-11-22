package submit.repository;

// $Id: RsasFormRepository.java,v 1.15 2016-10-04 23:35:35-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import submit.entity.RsasForm;

public interface RsasFormRepository extends JpaRepository<RsasForm, Integer>{
    RsasForm findById(int id);
    @Query(value = "SELECT r FROM RsasForm r where r.ssasForm.id=?1 order by transferdate asc")
    List<RsasForm> findAllForSsaOrderByTransferdateAsc(int ssaid);
    List<RsasForm> findByApprovedTrueOrderByTransferdateAsc();  // approved
    List<RsasForm> findByApprovedFalseOrderByTransferdateAsc(); // draft
}
