package ru.moore.AISUchetTehniki.models.Entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;
import ru.moore.AISUchetTehniki.models.Entity.spr.Organization;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REASON")
@Where(clause = "deleted = false")
@SuperBuilder
public class Reason extends BaseEntity implements Serializable {

    @Column(name = "type_record")
    @NotBlank
    private String typeRecord;

    @Column(name = "date")
    @NotNull
    private Date date;

    @Column(name = "number")
    @NotBlank
    private String number;

    @Column(name = "sum")
    private double sum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_id")
    private Counterparty counterparty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

}
