package ru.moore.AISUchetTehniki.models.Dto.reason;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.spr.CounterpartyDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationNotChildrenDto;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Validated
public class ReasonStatementDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class, View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'date' не может быть пустым.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'number' не может быть пустым.")
    private String number;

    @JsonView({View.RESPONSE.class})
    private OrganizationNotChildrenDto organization;

    @JsonView({View.SAVE.class})
    private UUID organizationId;

    @JsonView({View.SAVE.class})
    private @Valid List<ReasonStatementSpecDto> spec;

    public void setNumber(String number) {
        String result = number.replaceAll("\\s+", " ");
        this.number = result.trim();
    }

    @Data
    @NoArgsConstructor
    public static class ReasonStatementSpecDto {
        @JsonView({View.SAVE.class})
        @NotNull(groups = {OnSave.class}, message = "Поле 'id' не может быть пустым.")
        private UUID id;
    }

}
