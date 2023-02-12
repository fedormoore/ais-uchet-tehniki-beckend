package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RemoveDeviceDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.RemoveDeviceService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/remove_device")
@RequiredArgsConstructor
@Validated
public class RemoveDeviceController {

    private final RemoveDeviceService removeDeviceService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveAddDevice(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<RemoveDeviceDto> removeDeviceDtoList) {
        return removeDeviceService.saveRemoveDevice(removeDeviceDtoList);
    }

}
