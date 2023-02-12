package ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@Validated
public class CabinetToStorageDto {

    @JsonView({View.SAVE.class})
    private UUID statementId;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'materialValueOrgId' не может быть пустым.")
    private UUID materialValueOrgId;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'locationId' не может быть пустым.")
    private UUID locationId;

}
