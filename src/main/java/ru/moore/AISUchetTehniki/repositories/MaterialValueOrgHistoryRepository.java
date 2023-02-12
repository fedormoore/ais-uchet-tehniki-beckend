package ru.moore.AISUchetTehniki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;

import java.util.List;
import java.util.UUID;

public interface MaterialValueOrgHistoryRepository extends JpaRepository<MaterialValueOrgHistory, UUID> {

    List<MaterialValueOrgHistory> findAllByMaterialValueOrgIdOrderByCreatedAtAsc(UUID registryId);

    List<MaterialValueOrgHistory> findAllByReasonIdAndTypeInOrderByTypeAsc(UUID reasonId, List<String> type);

    List<MaterialValueOrgHistory> findAllByReasonIdAndParentIdIsNull(UUID id);

    List<MaterialValueOrgHistory> findAllByTypeInAndParentIdIsNullAndReasonIsNullOrReasonId(List<String> typeList, UUID id);

    List<MaterialValueOrgHistory> findAllByTypeInAndParentIdIsNullAndReasonIsNull(List<String> typeList);

    List<MaterialValueOrgHistory> findAllByTypeNotInAndParentIdIsNullAndReasonIsNullOrReasonId(List<String> typeList, UUID id);

    List<MaterialValueOrgHistory> findAllByTypeNotInAndParentIdIsNullAndReasonIsNull(List<String> typeList);

    int countByTypeAndMaterialValueOrgId(String type, UUID id);

    List<MaterialValueOrgHistory> findAllByReasonId(UUID id);

    List<MaterialValueOrgHistory> findByReasonId(UUID id);

}
