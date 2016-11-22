package submit.entity;

// $Id: UsersForm.java,v 1.8 2016-10-31 12:50:43-04 ericholp Exp $

import javax.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
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
