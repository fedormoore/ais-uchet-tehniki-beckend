package ru.moore.AISUchetTehniki.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgHistoryDto;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/device_history")
@RequiredArgsConstructor
public class MaterialValueOrgHistoryController {

    private final MaterialValueOrgHistoryService historyService;

    @JsonView(View.RESPONSE.class)
    @GetMapping("/registry/{id}")
    public List<MaterialValueOrgHistoryDto> getAllHistoryByRegistryId(@PathVariable UUID id) {
        return historyService.getAllHistoryByRegistryId(id);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/reason/{id}")
    public List<MaterialValueOrgHistoryDto> getAllHistoryByReasonId(@PathVariable UUID id) {
        return historyService.getAllHistoryByReasonId(id);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<MaterialValueOrgHistoryDto> saveHistory(@JsonView(View.SAVE.class) @Valid @RequestBody List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList) {
        return historyService.saveHistory(materialValueOrgHistoryDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @DeleteMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnDelete.class)
    public List<MaterialValueOrgHistoryDto> deleteReasonInHistory(@JsonView(View.DELETE.class) @Valid @RequestBody List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList) {
        return historyService.deleteReasonInHistory(materialValueOrgHistoryDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/contract/{id}")
    public List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNullOrByReason(@PathVariable UUID id) {
        return historyService.findAllForContractByReasonIsNullOrByReason(id);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/contract")
    public List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNull() {
        return historyService.findAllForContractByReasonIsNull();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/statement/{id}")
    public List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNullOrByReason(@PathVariable UUID id) {
        return historyService.findAllForStatementByReasonIsNullOrByReason(id);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/statement")
    public List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNull() {
        return historyService.findAllForStatementByReasonIsNull();
    }
}
