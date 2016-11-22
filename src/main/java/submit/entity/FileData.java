package submit.entity;

// $Id: FileData.java,v 1.5 2016-11-02 15:46:53-04 ericholp Exp $

import lombok.Data;

@Data
public class FileData {
    private String name;
    private long size;
    private String lastmoddatetime;
    private String nicesize;
    private String status;
    private String setmoddatetimestatus;
}
