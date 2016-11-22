package submit.entity;

// $Id: Defaults.java,v 1.3 2016-11-02 15:29:27-04 ericholp Exp $

import lombok.Data;

@Data
public class Defaults {
    private String recordstitle="";
    private String copyrightstatement="";
    private String retentionschedule="";
    private String retentionperiod="";
    private String archivedescriptionstandards="";
    private String accessrestriction="";
}
