package submit.entity;

import javax.persistence.*;

import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "map")
public class MapForm implements Serializable {

    @Column(name = "userid", insertable = false, updatable = false)
    private Integer userid;

    @Column(name = "departmentid", insertable = false, updatable = false)
    private Integer departmentid;

    private boolean departmentactive = false;

    @EmbeddedId
    IdKey key;
}
