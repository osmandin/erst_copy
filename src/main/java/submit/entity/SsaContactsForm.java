package submit.entity;

// $Id: SsaContactsForm.java,v 1.16 2016-11-03 14:59:46-04 ericholp Exp $

import javax.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude={"ssasForm"})
@Entity
@Table(name = "ssaContacts")
public class SsaContactsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name="";
    private String phone="";
    private String address="";
    private String email="";
    private int idx;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ssaid")
    private SsasForm ssasForm;
}
