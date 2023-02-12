package ru.moore.AISUchetTehniki.controllers.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueDto;
import ru.moore.AISUchetTehniki.services.spr.MaterialValueService;
import ru.moore.AISUchetTehniki.specifications.MaterialValueSpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_material_value")
@RequiredArgsConstructor
@Validated
public class MaterialValueController {

    private final MaterialValueService materialValueService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public Page<MaterialValueDto> getAllMaterialValuePage(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        return materialValueService.getAllMaterialValuePage(MaterialValueSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<MaterialValueDto> saveDevice(@JsonView(View.SAVE.class) @Valid @RequestBody List<MaterialValueDto> materialValueDtoList) {
        if (materialValueDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return materialValueService.saveMaterialValue(materialValueDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteMaterialValue(@JsonView(View.DELETE.class) @Valid @RequestBody List<MaterialValueDto> materialValueDtoList) {
        if (materialValueDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return materialValueService.deleteMaterialValue(materialValueDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/list")
    public List<MaterialValueDto> getAllMaterialValueList() {
        return materialValueService.getAllMaterialValueList();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/name_in_org_is_not_null_list")
    public List<MaterialValueDto> getAllMaterialValueByNameInOrgIsNotNull() {
        return materialValueService.getAllMaterialValueByNameInOrgIsNotNull();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/material_value_by_add_to_other_true_list")
    public List<MaterialValueDto> getAllMaterialValueByAddToOtherTrue() {
        return materialValueService.getAllMaterialValueByAddToOtherTrue();
    }

    @GetMapping("/firm_list")
    public List<String> getAllFirmList() {
        return materialValueService.getAllFirmList();
    }
}
