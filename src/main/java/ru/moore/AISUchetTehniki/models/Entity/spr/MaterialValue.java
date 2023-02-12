package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.Id;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

@Entity
@Table(name = "spr_material_value")
@Where(clause = "deleted = false")
public class MaterialValue extends BaseEntity implements Serializable {

    @Column(name = "index_b", insertable = false, updatable = false)
    private Integer indexB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_value_type_id")
    private MaterialValueType materialValueType;

    @Column(name = "name_in_org")
    private String nameInOrg;

    @Column(name = "name_firm")
    private String nameFirm;

    @Column(name = "name_model")
    private String nameModel;

}
