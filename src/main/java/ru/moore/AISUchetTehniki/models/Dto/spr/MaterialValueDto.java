package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class MaterialValueDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class,  View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class})
    private MaterialValueTypeDto materialValueType;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'materialValueTypeId' не может быть пустым.")
    private UUID materialValueTypeId;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String nameInOrg;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String nameFirm;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String nameModel;

    @AssertTrue(groups = OnSave.class, message = "Одно из полей 'nameInOrg' или 'nameFirm' с 'nameModel' должно быть заполнено")
    private boolean isNameInOrgAndNameFirmAndNameModelExists() {
        return nameInOrg != null || nameFirm != null || nameModel != null;
    }

    @AssertTrue(groups = OnSave.class, message = "Поле 'nameModel' должно быть заполнено")
    private boolean isNameModelExists() {
        return nameFirm == null || nameModel != null;
    }

    @AssertTrue(groups = OnSave.class, message = "Поле 'nameFirm' должно быть заполнено")
    private boolean isNameFirmExists() {
        return nameFirm != null || nameModel == null;
    }

    public void setNameInOrg(String nameInOrg) {
        String result = "";
        if (nameInOrg != null) {
            result = nameInOrg.replaceAll("\\s+", " ");
        }
        this.nameInOrg = result.trim();
    }

    public void setNameFirm(String nameFirm) {
        String result = "";
        if (nameFirm != null) {
            result = nameFirm.replaceAll("\\s+", " ");
        }
        this.nameFirm = result.trim();
    }

    public void setNameModel(String nameModel) {
        String result = "";
        if (nameModel != null) {
            result = nameModel.replaceAll("\\s+", " ");
        }
        this.nameModel = result.trim();
    }

}