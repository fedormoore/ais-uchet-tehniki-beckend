package ru.moore.AISUchetTehniki.models.Entity.spr;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import ru.moore.AISUchetTehniki.models.Entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spr_users")
@Where(clause = "deleted = false")
public class User extends BaseEntity implements Serializable {

    @Column(name = "email")
    @NotBlank
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "last_name")
    @NotBlank
    private String lastName;

    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @Column(name = "middle_names")
    private String middleNames;

    @Column(name = "organization_function")
    private String organizationFunction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organization_id")
    private Organization organization;

}