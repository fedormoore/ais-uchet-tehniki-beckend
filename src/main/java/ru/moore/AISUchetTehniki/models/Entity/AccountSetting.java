package ru.moore.AISUchetTehniki.models.Entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts_setting")
@Where(clause = "deleted = false")
@SuperBuilder
public class AccountSetting extends BaseEntity implements Serializable {

    @Column(name="preamble_statement_report")
    private String preambleStatementReport;

}