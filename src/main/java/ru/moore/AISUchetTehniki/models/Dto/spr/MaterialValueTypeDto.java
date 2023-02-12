package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class MaterialValueTypeDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class,  View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'name' не может быть пустым.")
    private String name;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private boolean addToOther;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private boolean addOther;

    public void setName(String name) {
        String result = name.replaceAll("\\s+", " ");
        this.name = result.trim();
    }
}
