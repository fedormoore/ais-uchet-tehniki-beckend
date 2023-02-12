package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class OrganizationDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class, View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'type' не может быть пустым.")
    private String type;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'name' не может быть пустым.")
    private String name;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private @Valid List<OrganizationDto> children;

    public void setName(String name) {
        String result = name.replaceAll("\\s+", " ");
        this.name = result.trim();
    }
}
