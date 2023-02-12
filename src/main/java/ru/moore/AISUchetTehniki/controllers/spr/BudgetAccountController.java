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
import ru.moore.AISUchetTehniki.models.Dto.spr.BudgetAccountDto;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.specifications.BudgetAccountSpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_budget_account")
@RequiredArgsConstructor
@Validated
public class BudgetAccountController {

    private final BudgetAccountService budgetAccountService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public Page<BudgetAccountDto> getAllBudgetAccount(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        return budgetAccountService.getAllBudgetAccountPage(BudgetAccountSpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<BudgetAccountDto> saveBudgetAccount(@JsonView(View.SAVE.class) @Valid @RequestBody List<BudgetAccountDto> budgetAccountDtoList) {
        if (budgetAccountDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return budgetAccountService.saveBudgetAccount(budgetAccountDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteBudgetAccount(@JsonView(View.DELETE.class) @Valid @RequestBody List<BudgetAccountDto> budgetAccountDtoList) {
        if (budgetAccountDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return budgetAccountService.deleteBudgetAccount(budgetAccountDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/list")
    public List<BudgetAccountDto> getAllBudgetAccountList() {
        return budgetAccountService.getAllBudgetAccountList();
    }

}
