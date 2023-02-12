package ru.moore.AISUchetTehniki.services.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.spr.BudgetAccountDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.repositories.spr.BudgetAccountRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetAccountService {

    private final BudgetAccountRepository budgetAccountRepository;

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MapperUtils mapperUtils;

    public Page<BudgetAccountDto> getAllBudgetAccountPage(Specification<BudgetAccount> spec, int page, int pageSize) {
        return budgetAccountRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "code"))).map(budgetAccount -> toDtoBudgetAccount(budgetAccount));
    }

    public List<BudgetAccountDto> getAllBudgetAccountList() {
        return mapperUtils.mapAll(budgetAccountRepository.findAll(), BudgetAccountDto.class);
    }

    @Transactional
    public List<BudgetAccountDto> saveBudgetAccount(List<BudgetAccountDto> budgetAccountDtoList) {
        List<BudgetAccount> returnBudgetAccount = new ArrayList<>();
        for (BudgetAccountDto budgetAccountDto : budgetAccountDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
            BudgetAccount budgetAccount = findById(budgetAccountDto.getId()).orElse(new BudgetAccount());
            budgetAccount.setCode(budgetAccountDto.getCode());
            budgetAccount.setName(budgetAccountDto.getName());
            budgetAccountRepository.save(budgetAccount);
            returnBudgetAccount.add(budgetAccount);
        }
        return mapperUtils.mapAll(returnBudgetAccount, BudgetAccountDto.class);
    }

    @Transactional
    public ResponseEntity<?> deleteBudgetAccount(List<BudgetAccountDto> budgetAccountDtoList) {
        for (BudgetAccountDto budgetAccountDto : budgetAccountDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            BudgetAccount budgetAccount = findById(budgetAccountDto.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByBudgetAccountId(budgetAccount.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            budgetAccount.setDeleted(true);
            budgetAccountRepository.save(budgetAccount);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private BudgetAccountDto toDtoBudgetAccount(BudgetAccount budgetAccount) {
        return mapperUtils.map(budgetAccount, BudgetAccountDto.class);
    }

    public Optional<BudgetAccount> findById(UUID id) {
        if (id != null) {
            Optional<BudgetAccount> budgetAccountFind = budgetAccountRepository.findById(id);
            if (budgetAccountFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Бюджетный счет с ID " + id + " не найден!");
            }
            return budgetAccountFind;
        } else {
            return Optional.empty();
        }
    }
}
