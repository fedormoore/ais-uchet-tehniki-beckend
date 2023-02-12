package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.enums.OrganizationTypeEnum;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder

@Entity
@Table(name = "spr_organization")
@Where(clause = "deleted = false")
public class Organization extends BaseEntity implements Serializable {

    @Column(name = "type")
    @NotBlank
    private String type;

    public String getType() {
        return OrganizationTypeEnum.convertToEntityAttribute(this.type);
    }

    public void setType(String type) {
        this.type = OrganizationTypeEnum.convertToDatabaseColumn(type);
    }

    @Column(name = "name")
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Organization> children;


}