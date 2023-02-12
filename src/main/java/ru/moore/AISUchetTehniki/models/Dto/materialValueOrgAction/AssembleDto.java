package ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Validated
public class AssembleDto {

    @JsonView({View.SAVE.class})
    private UUID statementId;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'materialValueId' не может быть пустым.")
    private UUID materialValueId;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'locationId' не может быть пустым.")
    private UUID locationId;

    @JsonView({View.SAVE.class})
    private UUID responsibleId;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'budgetAccountId' не может быть пустым.")
    private UUID budgetAccountId;

    @JsonView({View.SAVE.class})
    private @Valid List<AssembleSpecDto> specification;

    @Data
    @NoArgsConstructor
    public static class AssembleSpecDto {

        @JsonView({View.SAVE.class})
        @NotNull(groups = {OnSave.class}, message = "Поле 'id' не может быть пустым.")
        private UUID id;

    }

}
