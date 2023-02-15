package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.spr.BudgetAccountDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetAccountService {
    Page<BudgetAccountDto> getAllBudgetAccountPage(Specification<BudgetAccount> build, int page, int limit);

    List<BudgetAccountDto> saveBudgetAccount(List<BudgetAccountDto> budgetAccountDtoList);

    ResponseEntity<?> deleteBudgetAccount(List<BudgetAccountDto> budgetAccountDtoList);

    List<BudgetAccountDto> getAllBudgetAccountList();

    Optional<BudgetAccount> findById(UUID id);
}
