package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.CabinetToStorageDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.CabinetToStorageService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/cabinet_to_storage")
@RequiredArgsConstructor
@Validated
public class CabinetToStorageController {

    private final CabinetToStorageService cabinetToStorageService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveStorageToRegistry(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<CabinetToStorageDto> cabinetToStorageDtoList) {
        return cabinetToStorageService.saveCabinetToStorage(cabinetToStorageDtoList);
    }

}
