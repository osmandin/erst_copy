package submit.repository;

// $Id: RsaFileDataFormRepository.java,v 1.3 2016-10-04 23:35:24-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import submit.entity.RsaFileDataForm;

import java.util.List;

public interface RsaFileDataFormRepository extends JpaRepository<RsaFileDataForm, Integer>{
     public RsaFileDataForm findById(int id);
    @Query(value = "SELECT f FROM RsaFileDataForm f JOIN f.rsasForm r where r.id=:rsaid and f.name=:filename order by f.name")
    List<RsaFileDataForm> findBasedOnIdAndFilename(@Param("rsaid") int rsaid, @Param("filename") String filename);
}
