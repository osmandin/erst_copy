package submit.entity;

// $Id: OnlineSubmissionRequestForm.java,v 1.6 2016-10-22 01:14:50-04 ericholp Exp $

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "onlineSubmissionRequest")
public class OnlineSubmissionRequestForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int ssaid;
    private String department;
    private String address;
    private String name;
    private String email;
    private String phone;
    private String departmenthead;
    private String signature;
    private String date;

    @Transient
    private String nicedate;

}
