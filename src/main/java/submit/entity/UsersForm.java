package submit.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@Entity
@ToString(exclude={"departmentsForms"})
@Table(name = "users")
public class UsersForm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String email;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
	       name = "map",
	       joinColumns = @JoinColumn(name = "userid", referencedColumnName = "id"),
	       inverseJoinColumns = @JoinColumn(name = "departmentid", referencedColumnName = "id")
	       )
    private List<DepartmentsForm> departmentsForms;    
}
