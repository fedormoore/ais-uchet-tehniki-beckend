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
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueTypeDto;
import ru.moore.AISUchetTehniki.services.spr.MaterialValueTypeService;
import ru.moore.AISUchetTehniki.specifications.MaterialValueTypeSpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_material_value_type")
@RequiredArgsConstructor
@Validated
public class MaterialValueTypeController {

    private final MaterialValueTypeService materialValueTypeService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public Page<MaterialValueTypeDto> getAllMaterialValueTypePage(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        return materialValueTypeService.getAllMaterialValueTypePage(MaterialValueTypeSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<MaterialValueTypeDto> saveMaterialValueType(@JsonView(View.SAVE.class) @Valid @RequestBody List<MaterialValueTypeDto> materialValueTypeDtoList) {
        if (materialValueTypeDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return materialValueTypeService.saveMaterialValueType(materialValueTypeDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteMaterialValueType(@JsonView(View.DELETE.class) @Valid @RequestBody List<MaterialValueTypeDto> materialValueTypeDtoList) {
        if (materialValueTypeDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return materialValueTypeService.deleteMaterialValueType(materialValueTypeDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/list")
    public List<MaterialValueTypeDto> getAllMaterialValueTypeList() {
        return materialValueTypeService.getAllMaterialValueTypeList();
    }
}
