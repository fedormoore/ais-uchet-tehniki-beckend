package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.StorageToCabinetDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.StorageToCabinetService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/storage_to_cabinet")
@RequiredArgsConstructor
@Validated
public class StorageToCabinetController {

    private final StorageToCabinetService storageToCabinetService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveStorageToCabinet(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<StorageToCabinetDto> storageToCabinetDtoList) {
        return storageToCabinetService.saveStorageToCabinet(storageToCabinetDtoList);
    }

}
