package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;

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
@Table(name = "spr_location")
@Where(clause = "deleted = false")
public class Location extends BaseEntity implements Serializable {

    @Column(name = "type")
    @NotBlank
    private String type;

    public String getType() {
        return LocationTypeEnum.convertToEntityAttribute(this.type);
    }

    public void setType(String type) {
        this.type = LocationTypeEnum.convertToDatabaseColumn(type);
    }

    @Column(name = "name")
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    private List<User> userList;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    private List<MaterialValueOrg> materialValueOrgList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Location> children;

}
