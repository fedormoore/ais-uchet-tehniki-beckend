package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SPR_BUDGET_ACCOUNT")
@Where(clause = "deleted = false")
public class BudgetAccount extends BaseEntity implements Serializable {

    @Column(name = "code")
    @NotBlank
    private String code;

    @Column(name = "name")
    @NotBlank
    private String name;

}