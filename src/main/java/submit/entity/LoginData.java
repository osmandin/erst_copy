package submit.entity;

// $Id: LoginData.java,v 1.5 2016-10-04 23:29:35-04 ericholp Exp $

import lombok.Data;

@Data
public class LoginData {
    private String username;
    private String password;
    private String hostname;
}
