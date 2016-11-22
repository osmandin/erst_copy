package submit.entity;

// $Id: EmailSetup.java,v 1.13 2016-10-25 17:55:57-04 ericholp Exp $

import lombok.Data;
import java.util.Arrays;

@Data
public class EmailSetup {
    private String from;
    private String to;
    private String[] toarray;
    private String subject;
    private String username;
    private String firstname;
    private String lastname;
    private boolean isadmin;
    private String body;
    private String toarrayasstr;

    public void setToarray(String[] a){
	toarray = a;
	if(a != null){
	    toarrayasstr=Arrays.toString(a);
	    toarrayasstr=toarrayasstr.substring(1).substring(0, toarrayasstr.length() - 2);
	}
    }
}
