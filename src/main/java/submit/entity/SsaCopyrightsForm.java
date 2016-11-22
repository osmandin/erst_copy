package submit.entity;

// $Id: SsaCopyrightsForm.java,v 1.21 2016-10-31 12:48:40-04 ericholp Exp $

import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "ssaCopyrights")
public class SsaCopyrightsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String copyright;

    private int idx;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ssaid")
    private SsasForm ssasForm;
}
