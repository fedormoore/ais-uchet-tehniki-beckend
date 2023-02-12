package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SPR_COUNTERPARTY")
@Where(clause = "deleted = false")
public class Counterparty extends BaseEntity implements Serializable {

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "inn")
    private String inn;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "contact")
    private String contact;

}
