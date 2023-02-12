package ru.moore.AISUchetTehniki.controllers.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationTypeDto;
import ru.moore.AISUchetTehniki.services.spr.OrganizationService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_organization")
@RequiredArgsConstructor
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public List<OrganizationDto> getAllOrganizationList(@RequestParam MultiValueMap<String, String> params) {
        return organizationService.getAllOrganizationList(params);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<OrganizationDto> saveOrganization(@JsonView(View.SAVE.class) @Valid @RequestBody List<OrganizationDto> organizationDtoList) {
        if (organizationDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return organizationService.saveOrganization(organizationDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteOrganization(@JsonView(View.DELETE.class) @Valid @RequestBody List<OrganizationDto> organizationDtoList) {
        if (organizationDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return organizationService.deleteOrganization(organizationDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/type_list")
    public List<OrganizationTypeDto> getAllOrganizationType() {
        return organizationService.getAllOrganizationType();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/only_org_list")
    public List<OrganizationDto> getAllOrganizationTypeOrg() {
        return organizationService.getAllOrganizationTypeOrg();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/only_str_list")
    public List<OrganizationDto> getAllStructureTypeStr() {
        return organizationService.getAllStructureTypeStr();
    }

}
