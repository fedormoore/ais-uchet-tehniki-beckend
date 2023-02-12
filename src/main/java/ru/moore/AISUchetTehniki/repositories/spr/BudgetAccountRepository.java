package ru.moore.AISUchetTehniki.repositories.spr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;

import java.util.UUID;

@Repository
public interface BudgetAccountRepository extends JpaRepository<BudgetAccount, UUID>, JpaSpecificationExecutor<BudgetAccount> {

}
