package ru.moore.AISUchetTehniki.repositories.spr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;

import java.util.UUID;

public interface CounterpartyRepository extends JpaRepository<Counterparty, UUID>, JpaSpecificationExecutor<Counterparty> {

}
