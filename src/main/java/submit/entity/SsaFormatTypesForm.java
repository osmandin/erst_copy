package submit.entity;

// $Id: SsaFormatTypesForm.java,v 1.10 2016-10-31 12:48:50-04 ericholp Exp $

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ssaFormatTypes")
public class SsaFormatTypesForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String formattype;
    private int idx;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ssaid")
    private SsasForm ssasForm;
}
