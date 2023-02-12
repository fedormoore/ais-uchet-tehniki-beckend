package ru.moore.AISUchetTehniki.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgNotChildrenDto;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.specifications.MaterialValueOrgSpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/material_value_org")
@RequiredArgsConstructor
public class MaterialValueOrgController {

    private final MaterialValueOrgService materialValueOrgService;

    @GetMapping("/storage")
    public Page<MaterialValueOrgDto> getAllMaterialValueOrgStorage(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        params.add("locationType", "storage");
        return materialValueOrgService.getAllMaterialValueOrgByLocation(MaterialValueOrgSpecifications.build(params), page, limit);
    }

    @GetMapping("/cabinet")
    public Page<MaterialValueOrgDto> getAllMaterialValueOrgLocation(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        params.add("locationType", "cabinet");
        return materialValueOrgService.getAllMaterialValueOrgByLocation(MaterialValueOrgSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<MaterialValueOrgDto> saveMaterialValueOrg(@JsonView(View.SAVE.class) @Valid @RequestBody List<MaterialValueOrgDto> materialValueOrgDtoDtoList) {
        return materialValueOrgService.saveMaterialValueOrg(materialValueOrgDtoDtoList);
    }

    @GetMapping("/all_material_value_org_not_expandable")
    public List<MaterialValueOrgNotChildrenDto> getAllMaterialValueOrgNotExpandable() {
        return materialValueOrgService.getAllMaterialValueOrgNotExpandable();
    }

    @GetMapping("/all_material_value_org_parent_is_null")
    public List<MaterialValueOrgDto> getAllMaterialValueOrgParentIdIsNullExpandable() {
        return materialValueOrgService.getAllMaterialValueOrgParentIdIsNullExpandable();
    }

    @GetMapping("/all_material_value_org_children_is_null")
    public List<MaterialValueOrgDto> getAllMaterialValueOrgChildrenIdIsNull() {
        return materialValueOrgService.getAllMaterialValueOrgChildrenIdIsNull();
    }

    @GetMapping("/all_material_value_org_name_in_org_is_not_null")
    public List<MaterialValueOrgDto> getAllMaterialValueOrgNameInOrgIsNotNull() {
        return materialValueOrgService.getAllMaterialValueOrgNameInOrgIsNotNull();
    }

    @GetMapping("/all_material_value_org_by_add_to_other_true_and_paren_is_null")
    public List<MaterialValueOrgDto> getAllMaterialValueOrgByAddToOtherTrue() {
        return materialValueOrgService.getAllMaterialValueOrgByAddToOtherTrue();
    }

    @GetMapping("/all_material_value_org_by_add_other_true")
    public List<MaterialValueOrgDto> getAllMaterialValueOrgByAddOtherTrue() {
        return materialValueOrgService.getAllMaterialValueOrgByAddOtherTrue();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/cartridge_need_refilling_list")
    public List<MaterialValueOrgDto> getAllCartridgeNeedRefiling() {
        return materialValueOrgService.getAllCartridgeNeedRefiling();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/printer_in_cabinet_list")
    public List<MaterialValueOrgDto> getAllPrinter() {
        return materialValueOrgService.getAllPrinter();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/cartridge_full_list")
    public List<MaterialValueOrgDto> getAllCartridgeFull() {
        return materialValueOrgService.getAllCartridgeFull();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/dispose_of")
    public List<MaterialValueOrgDto> getDisposeOf() {
        return materialValueOrgService.getAllDisposeOf();
    }

}
