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
import ru.moore.AISUchetTehniki.models.Dto.spr.CounterpartyDto;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;
import ru.moore.AISUchetTehniki.repositories.ReasonRepository;
import ru.moore.AISUchetTehniki.repositories.spr.CounterpartyRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;
    private final ReasonRepository reasonRepository;
    private final MapperUtils mapperUtils;

    public Page<CounterpartyDto> getAllCounterpartyPage(Specification<Counterparty> spec, int page, int pageSize) {
        return counterpartyRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "name"))).map(counterparty -> toDtoCounterparty(counterparty));
    }

    public List<CounterpartyDto> getAllCounterpartyList() {
        return mapperUtils.mapAll(counterpartyRepository.findAll(), CounterpartyDto.class);
    }

    @Transactional
    public List<CounterpartyDto> saveCounterparty(List<CounterpartyDto> counterpartyDtoList) {
        List<Counterparty> returnCounterparty = new ArrayList<>();
        for (CounterpartyDto counterpartyDto : counterpartyDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ COUNTERPARTY ПО ID
            Counterparty counterparty = findById(counterpartyDto.getId()).orElse(new Counterparty());
            counterparty.setName(counterpartyDto.getName());
            counterparty.setInn(counterpartyDto.getInn());
            counterparty.setTelephone(counterpartyDto.getTelephone());
            counterparty.setEmail(counterpartyDto.getEmail());
            counterparty.setContact(counterpartyDto.getContact());

            counterpartyRepository.save(counterparty);
            returnCounterparty.add(counterparty);
        }
        return mapperUtils.mapAll(returnCounterparty, CounterpartyDto.class);
    }

    @Transactional
    public ResponseEntity<?> deleteCounterparty(List<CounterpartyDto> counterpartyDtoList) {
        for (CounterpartyDto counterpartyDto : counterpartyDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ COUNTERPARTY ПО ID
            Counterparty counterparty = findById(counterpartyDto.getId()).orElse(null);

            List<Reason> reason = reasonRepository.findByCounterpartyId(counterparty.getId());
            if (reason.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            counterparty.setDeleted(true);
            counterpartyRepository.save(counterparty);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private CounterpartyDto toDtoCounterparty(Counterparty counterparty) {
        return mapperUtils.map(counterparty, CounterpartyDto.class);
    }

    public Optional<Counterparty> findById(UUID id) {
        if (id != null) {
            Optional<Counterparty> counterpartyFind = counterpartyRepository.findById(id);
            if (counterpartyFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Контрагент с ID " + id + " не найден!");
            }
            return counterpartyFind;
        } else {
            return Optional.empty();
        }
    }
}
