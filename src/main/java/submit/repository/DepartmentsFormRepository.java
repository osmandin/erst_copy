package submit.repository;

// $Id: DepartmentsFormRepository.java,v 1.12 2016-10-21 09:02:19-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import submit.entity.DepartmentsForm;

import java.util.List;

public interface DepartmentsFormRepository extends JpaRepository<DepartmentsForm, Integer>{
    DepartmentsForm findById(int departmentid);
    List<DepartmentsForm> findByName(String name);
    @Query(value = "SELECT d FROM DepartmentsForm d order by name asc")
    List<DepartmentsForm> findAllOrderByNameAsc();    
    @Query(value = "SELECT d FROM DepartmentsForm d JOIN d.usersForms u where d.id=:departmentid and u.id=:userid order by d.name")
    List<DepartmentsForm> findBasedOnIdAndUserid(@Param("departmentid") int departmentid, @Param("userid") int userid);
}
