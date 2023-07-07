package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgHistoryDto;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonContractDto;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonStatementDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;
import ru.moore.AISUchetTehniki.models.Entity.spr.Organization;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.repositories.ReasonRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.spr.CounterpartyService;
import ru.moore.AISUchetTehniki.services.spr.OrganizationService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReasonServiceImpl implements ReasonService {

    private final ReasonRepository reasonRepository;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;
    private final CounterpartyService counterpartyService;
    private final OrganizationService organizationService;
    private final MapperUtils mapperUtils;

    @Override
    public Page<ReasonContractDto> getAllReasonPage(Specification<Reason> spec, int page, int pageSize) {
        return reasonRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "date"))).map(docMain -> toDtoReason(docMain));
    }

    @Override
    @Transactional
    public ReasonContractDto saveContractDTO(ReasonContractDto reasonContractDto) {
        //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ REASON ПО ID
        Reason contract = findById(reasonContractDto.getId()).orElse(new Reason());
        //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ COUNTERPARTY ПО ID
        Counterparty counterparty = counterpartyService.findById(reasonContractDto.getCounterpartyId()).orElse(null);
        //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
        Organization organization = organizationService.findById(reasonContractDto.getOrganizationId()).orElse(null);

        contract.setTypeRecord("contract");
        contract.setDate(reasonContractDto.getDate());
        contract.setNumber(reasonContractDto.getNumber());
        contract.setSum(reasonContractDto.getSum());
        contract.setCounterparty(counterparty);
        contract.setOrganization(organization);

        reasonRepository.save(contract);

        List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList = new ArrayList<>();
        if (reasonContractDto.getSpec() != null) {
            for (ReasonContractDto.ContractSpecDto spec : reasonContractDto.getSpec()) {
                MaterialValueOrgHistoryDto materialValueOrgHistoryDto = new MaterialValueOrgHistoryDto();
                materialValueOrgHistoryDto.setId(spec.getId());
                materialValueOrgHistoryDto.setReasonId(contract.getId());
                materialValueOrgHistoryDtoList.add(materialValueOrgHistoryDto);
            }
        }
        materialValueOrgHistoryService.saveHistory(materialValueOrgHistoryDtoList);

        return mapperUtils.map(contract, ReasonContractDto.class);
    }

    @Override
    public Reason saveReason(Reason reason) {
        reasonRepository.save(reason);
        return reason;
    }

    @Override
    @Transactional
    public ReasonStatementDto saveStatementDTO(ReasonStatementDto reasonStatementDto) {
        //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ REASON ПО ID
        Reason statement = findById(reasonStatementDto.getId()).orElse(new Reason());
        //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
        Organization organization = organizationService.findById(reasonStatementDto.getOrganizationId()).orElse(null);

        statement.setTypeRecord("statement");
        statement.setDate(reasonStatementDto.getDate());
        statement.setNumber(reasonStatementDto.getNumber());
        statement.setOrganization(organization);

        reasonRepository.save(statement);

        List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList = new ArrayList<>();
        if (reasonStatementDto.getSpec() != null) {
            for (ReasonStatementDto.ReasonStatementSpecDto spec : reasonStatementDto.getSpec()) {
                MaterialValueOrgHistoryDto materialValueOrgHistoryDto = new MaterialValueOrgHistoryDto();
                materialValueOrgHistoryDto.setId(spec.getId());
                materialValueOrgHistoryDto.setReasonId(statement.getId());
                materialValueOrgHistoryDtoList.add(materialValueOrgHistoryDto);
            }
        }
        materialValueOrgHistoryService.saveHistory(materialValueOrgHistoryDtoList);

        return mapperUtils.map(statement, ReasonStatementDto.class);
    }

    @Override
    public ResponseEntity<?> deleteReason(List<ReasonContractDto> reasonContractDtoList) {
        try {
            for (ReasonContractDto reasonContractDto : reasonContractDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ REASON ПО ID
                Reason reason = findById(reasonContractDto.getId()).orElse(null);

                List<MaterialValueOrgHistory> materialValueOrgHistories = materialValueOrgHistoryRepository.findByReasonId(reason.getId());
                if (materialValueOrgHistories.size() > 0) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
                }

                reason.setDeleted(true);
                reasonRepository.save(reason);
            }

        } catch (DataAccessException e) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(e.getRootCause()).getMessage());
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private ReasonContractDto toDtoReason(Reason contract) {
        return mapperUtils.map(contract, ReasonContractDto.class);
    }

    @Override
    public Optional<Reason> findById(UUID id) {
        if (id != null) {
            Optional<Reason> contractFind = reasonRepository.findById(id);
            if (contractFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Контракт с ID " + id + " не найден!");
            }
            return contractFind;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Reason> findByTypeRecordAndDateAndNumber(String typeRecord, Date date, String number) {
        if (!typeRecord.equals("") && !date.equals("") && !number.equals("")) {
            Optional<Reason> reasonFind = reasonRepository.findByTypeRecordAndDateAndNumber(typeRecord, date, number);
            return reasonFind;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ReasonContractDto> getAllContractList() {
        return mapperUtils.mapAll(reasonRepository.findAllByTypeRecord("contract"), ReasonContractDto.class);
    }

    @Override
    public List<ReasonContractDto> getAllStatementList() {
        return mapperUtils.mapAll(reasonRepository.findAllByTypeRecord("statement"), ReasonContractDto.class);
    }

}
