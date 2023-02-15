package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.AssembleDto;
import ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction.AssembleServiceImpl;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/assemble")
@RequiredArgsConstructor
@Validated
public class AssembleController {

    private final AssembleServiceImpl assembleServiceImpl;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveIncome(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<AssembleDto> assembleDtoList, Authentication authentication) {
        return assembleServiceImpl.saveAssemble(assembleDtoList, authentication);
    }

}
