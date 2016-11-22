package submit.entity;

// $Id: DepartmentsForm.java,v 1.24 2016-10-31 12:45:42-04 ericholp Exp $

import javax.persistence.*;
import lombok.*;
import java.util.List;

@Data
@Entity
@Table(name = "departments")
@EqualsAndHashCode(exclude={"ssasForm"})
public class DepartmentsForm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @Transient
    private boolean active; // used for usersForm

    @OneToOne(fetch=FetchType.EAGER, mappedBy="departmentForm")
    private SsasForm ssasForm;

    public int hashCode(SsasForm sf){
	return sf.getId();
    }
    
    @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
	       name = "map",
	       joinColumns = @JoinColumn(name = "departmentid", referencedColumnName = "id"),
	       inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "id")
	       )
    private List<UsersForm> usersForms;
}
