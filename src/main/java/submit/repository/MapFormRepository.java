package submit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import submit.entity.IdKey;
import submit.entity.MapForm;

public interface MapFormRepository extends JpaRepository<MapForm, IdKey>{
    public MapForm findByKey(IdKey idk);
}
