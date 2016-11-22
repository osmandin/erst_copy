package submit.entity;

// $Id: OrgInfo.java,v 1.4 2016-10-21 09:58:49-04 ericholp Exp $

import lombok.Data;

@Data
public class OrgInfo {
    private String email;
    private String phone;
    private String name;
    private String namefull;
}
