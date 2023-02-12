package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.DisposeOfDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.DisposeOfService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/dispose_of")
@RequiredArgsConstructor
@Validated
public class DisposeOfController {

    private final DisposeOfService disposeOfService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveDisposeOf(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<DisposeOfDto> disposeOfDtoList) {
        return disposeOfService.saveDisposeOf(disposeOfDtoList);
    }

}
