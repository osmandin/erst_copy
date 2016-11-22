package submit.entity;

// $Id: SsaAccessRestrictionsForm.java,v 1.9 2016-10-31 12:48:19-04 ericholp Exp $

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ssaAccessRestrictions")
public class SsaAccessRestrictionsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String restriction;

    private int idx;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ssaid")
    private SsasForm ssasForm;
}
