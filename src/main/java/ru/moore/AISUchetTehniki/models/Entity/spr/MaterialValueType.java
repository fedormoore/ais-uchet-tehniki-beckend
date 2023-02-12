package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spr_material_value_type")
@Where(clause = "deleted = false")
public class MaterialValueType extends BaseEntity implements Serializable {

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "add_to_other")
    @NotNull
    private boolean addToOther;

    @Column(name = "add_other")
    @NotNull
    private boolean addOther;

}
