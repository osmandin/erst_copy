package submit.entity;

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
