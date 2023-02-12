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
@Builder
@Entity
@Table(name = "accounts")
@Where(clause = "deleted = false")
public class Account implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "uuid4", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    @NotNull
    private UUID id;

    @Column(name = "index_b", insertable = false, updatable = false)
    private Integer indexB;

    @Column(name = "tenant_id", updatable = false)
    @NotNull
    private UUID tenantId;

    @Column(name = "email", updatable = false)
    @Email()
    @NotBlank()
    private String email;

    @Column(name="password")
    @NotBlank()
    private String password;

    @Column(name = "last_name")
    @NotBlank()
    private String lastName;

    @Column(name = "first_name")
    @NotBlank()
    private String firstName;

    @Column(name = "middle_names")
    private String middleNames;

    @Column(name = "confirmation_code")
    @NotBlank()
    private String confirmationCode;

    @Column(name = "confirmation")
    @NotNull
    private boolean confirmation;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

}