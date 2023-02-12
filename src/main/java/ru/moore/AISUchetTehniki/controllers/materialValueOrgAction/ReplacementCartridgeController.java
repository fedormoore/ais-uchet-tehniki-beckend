package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.ReplacementCartridgeDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.ReplacementCartridgeService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/replacement_cartridge")
@RequiredArgsConstructor
@Validated
public class ReplacementCartridgeController {

    private final ReplacementCartridgeService replacementCartridgeService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveReplacementCartridge(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<ReplacementCartridgeDto> replacementCartridgeDtoList) {
        return replacementCartridgeService.saveReplacementCartridge(replacementCartridgeDtoList);
    }

}
