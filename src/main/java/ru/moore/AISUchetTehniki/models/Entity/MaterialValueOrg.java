package ru.moore.AISUchetTehniki.models.Entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.models.Entity.spr.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MATERIAL_VALUE_ORG")
@Where(clause = "deleted = false")
@SuperBuilder
public class MaterialValueOrg extends BaseEntity implements Serializable {

    @Column(name = "barcode")
    @NotBlank
    private String barcode;

    @Column(name = "status")
    private String status;

    public String getStatus() {
        if (this.status != null) {
            return RegistryStatusEnum.convertToEntityAttribute(this.status);
        } else {
            return this.status;
        }

    }

    public void setStatus(String status) {
        if (status != null) {
            this.status = RegistryStatusEnum.convertToDatabaseColumn(status);
        } else {
            this.status = status;
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "material_value_id")
    private MaterialValue materialValue;

    @Column(name = "sum")
    private double sum;

    @Column(name = "inv_number")
    private String invNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_account_id")
    private BudgetAccount budgetAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id")
    private User responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MaterialValueOrg parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<MaterialValueOrg> children;

}
