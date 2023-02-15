package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.spr.CounterpartyDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CounterpartyService {
    Page<CounterpartyDto> getAllCounterpartyPage(Specification<Counterparty> build, int page, int limit);

    List<CounterpartyDto> saveCounterparty(List<CounterpartyDto> counterpartyDtoList);

    ResponseEntity<?> deleteCounterparty(List<CounterpartyDto> counterpartyDtoList);

    List<CounterpartyDto> getAllCounterpartyList();

    Optional<Counterparty> findById(UUID id);
}
