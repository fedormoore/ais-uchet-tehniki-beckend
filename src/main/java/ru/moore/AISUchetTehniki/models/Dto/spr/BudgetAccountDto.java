package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.*;

import javax.validation.constraints.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class BudgetAccountDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class,  View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'code' не может быть пустым.")
    private String code;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'name' не может быть пустым.")
    private String name;

    public void setCode(String code) {
        String result = code.replaceAll("\\s+", " ");
        this.code = result.trim();
    }

    public void setName(String name) {
        String result = name.replaceAll("\\s+", " ");
        this.name = result.trim();
    }
}
