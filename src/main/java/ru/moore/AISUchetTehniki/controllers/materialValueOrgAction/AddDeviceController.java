package ru.moore.AISUchetTehniki.controllers.materialValueOrgAction;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.AddDeviceDto;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.AddDeviceService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org/add_device")
@RequiredArgsConstructor
@Validated
public class AddDeviceController {

    private final AddDeviceService addDeviceService;

    @JsonView(View.RESPONSE.class)
    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveAddDevice(@JsonView(View.SAVE.class) @Valid @RequestBody(required = false) List<AddDeviceDto> addDeviceDtoList) {
        return addDeviceService.saveAddDevice(addDeviceDtoList);
    }

}
