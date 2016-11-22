package submit.repository;

// $Id: MapFormRepository.java,v 1.8 2016-10-23 06:58:43-04 ericholp Exp $

import org.springframework.data.jpa.repository.JpaRepository;

import submit.entity.IdKey;
import submit.entity.MapForm;

public interface MapFormRepository extends JpaRepository<MapForm, IdKey>{
    public MapForm findByKey(IdKey idk);
}
