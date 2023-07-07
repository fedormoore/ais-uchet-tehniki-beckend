package ru.moore.AISUchetTehniki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.moore.AISUchetTehniki.models.Entity.Reason;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReasonRepository extends JpaRepository<Reason, UUID>, JpaSpecificationExecutor<Reason> {

    List<Reason> findAllByTypeRecord(String typeRecord);

    List<Reason> findAllByTypeRecordAndIdIn(String typeRecord, List<UUID> id);

    List<Reason> findByCounterpartyId(UUID id);

    Optional<Reason> findByTypeRecordAndDateAndNumber(String typeRecord, Date date, String number);
}
