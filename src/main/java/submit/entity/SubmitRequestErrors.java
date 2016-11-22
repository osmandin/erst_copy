package submit.entity;

// $Id: SubmitRequestErrors.java,v 1.4 2016-10-31 12:50:19-04 ericholp Exp $

import lombok.Data;

@Data
public class SubmitRequestErrors {
    private boolean duplicateuser=false;
    private boolean duplicateusername=false;
    private boolean duplicatedepartment=false;
    private boolean fullDuplicates=false;
    private boolean departmentalreadyassignedtothisuser=false;
    private boolean useremailfailed=false;
    private boolean wrongnumberofusermatches=false;
    private int ssaid=-1;
}
