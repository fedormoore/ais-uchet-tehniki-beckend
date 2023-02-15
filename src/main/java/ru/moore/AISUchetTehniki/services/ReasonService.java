package ru.moore.AISUchetTehniki.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonContractDto;
import ru.moore.AISUchetTehniki.models.Dto.reason.ReasonStatementDto;
import ru.moore.AISUchetTehniki.models.Entity.Reason;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReasonService {
    Page<ReasonContractDto> getAllReasonPage(Specification<Reason> build, int page, int limit);

    ReasonContractDto saveContract(ReasonContractDto reasonContractDto);

    ReasonStatementDto saveStatement(ReasonStatementDto reasonStatementDto);

    ResponseEntity<?> deleteReason(List<ReasonContractDto> reasonContractDtoList);

    List<ReasonContractDto> getAllContractList();

    List<ReasonContractDto> getAllStatementList();

    Optional<Reason> findById(UUID id);
}
