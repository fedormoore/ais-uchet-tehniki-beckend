package ru.moore.AISUchetTehniki.models.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MATERIAL_VALUE_HISTORY")
@Where(clause = "deleted = false")
@SuperBuilder
public class MaterialValueOrgHistory extends BaseEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_value_org_id")
    private MaterialValueOrg materialValueOrg;

    @Column(name = "type")
    private String type;

    public String getType() {
        return HistoryTypeEnum.convertToEntityAttribute(this.type);
    }

    public void setType(String type) {
        this.type = HistoryTypeEnum.convertToDatabaseColumn(type);
    }

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason")
    private Reason reason;

    @Column(name = "note")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MaterialValueOrgHistory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<MaterialValueOrgHistory> children;

}
