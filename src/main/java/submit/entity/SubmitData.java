package submit.entity;

// $Id: SubmitData.java,v 1.7 2016-10-04 23:32:16-04 ericholp Exp $

import lombok.Data;

@Data
public class SubmitData {
    private String department;
    private String address;
    private String name;
    private String email;
    private String phone;
    private String departmenthead;
    private String signature;    
    private String date;
    private int ssaid;
    private String firstname;
    private String lastname;
    private String username;
}
