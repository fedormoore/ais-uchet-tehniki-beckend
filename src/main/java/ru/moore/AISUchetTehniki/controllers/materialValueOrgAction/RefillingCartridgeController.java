package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RefillingCartridgeDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.RefillingCartridgeService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/refilling_cartridge")
@RequiredArgsConstructor
@Validated
public class RefillingCartridgeController {

    private final RefillingCartridgeService refillingCartridgeService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveRefillingCartridge(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<RefillingCartridgeDto> refillingCartridgeDtoList) {
        return refillingCartridgeService.saveRefillingCartridge(refillingCartridgeDtoList);
    }

}
