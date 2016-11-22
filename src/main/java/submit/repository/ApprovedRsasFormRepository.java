package submit.repository;

// $Id: ApprovedRsasFormRepository.java,v 1.9 2016-10-23 06:57:46-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import submit.entity.ApprovedRsasForm;

import java.util.List;

public interface ApprovedRsasFormRepository extends JpaRepository<ApprovedRsasForm, Integer>{
    public ApprovedRsasForm findById(int id);

    @Query(value = "SELECT a FROM ApprovedRsasForm a order by datetime asc")
    List<ApprovedRsasForm> findAllOrderByDatetimeAsc();
}
