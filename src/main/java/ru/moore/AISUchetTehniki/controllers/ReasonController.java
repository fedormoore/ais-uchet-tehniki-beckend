package ru.moore.AISUchetTehniki.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonContractDto;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonStatementDto;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.specifications.ContractSpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/reason")
@RequiredArgsConstructor
@Validated
public class ReasonController {

    private final ReasonService reasonService;

    @JsonView(View.RESPONSE.class)
    @GetMapping("/contract")
    public Page<ReasonContractDto> getAllContract(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        params.add("typeRecord", "contract");
        return reasonService.getAllReasonPage(ContractSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/statement")
    public Page<ReasonContractDto> getAllStatement(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        params.add("typeRecord", "statement");
        return reasonService.getAllReasonPage(ContractSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping("/contract")
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public ReasonContractDto saveContract(@JsonView(View.SAVE.class) @Valid @RequestBody ReasonContractDto reasonContractDto) {
        return reasonService.saveContract(reasonContractDto);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping("/statement")
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnSave.class)
    public ReasonStatementDto saveStatement(@JsonView(View.SAVE.class) @Valid @RequestBody ReasonStatementDto reasonStatementDto) {
        return reasonService.saveStatement(reasonStatementDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteReason(@JsonView(View.DELETE.class) @Valid @RequestBody List<ReasonContractDto> reasonContractDtoList) {
        return reasonService.deleteReason(reasonContractDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/contract_list")
    public List<ReasonContractDto> getAllContractList() {
        return reasonService.getAllContractList();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/statement_list")
    public List<ReasonContractDto> getAllStatementList() {
        return reasonService.getAllStatementList();
    }

}
