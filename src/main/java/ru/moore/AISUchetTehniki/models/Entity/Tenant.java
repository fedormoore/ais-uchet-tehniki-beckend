package ru.moore.AISUchetTehniki.models.Entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TENANT")
@Where(clause = "deleted = false")
public class Tenant implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "tenant_id", updatable = false)
    @NotNull
    private UUID tenantId;

    @Column(name = "email", updatable = false)
    @Email
    @NotBlank
    private String email;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    public Tenant(UUID tenantId, String email) {
        this.tenantId = tenantId;
        this.email = email;
    }
}