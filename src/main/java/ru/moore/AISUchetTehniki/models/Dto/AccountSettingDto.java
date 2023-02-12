package ru.moore.AISUchetTehniki.models.Dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountSettingDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String preambleStatementReport;

}
