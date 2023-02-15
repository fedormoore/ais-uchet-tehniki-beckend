package ru.moore.AISUchetTehniki.services;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgHistoryDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public interface MaterialValueOrgHistoryService {
    List<MaterialValueOrgHistoryDto> getAllHistoryByRegistryId(UUID listMaterialValueIdId);

    @Transactional
    void saveFromStorageIncome(MaterialValueOrg materialValueOrgParent, List<MaterialValueOrg> MaterialValueOrgChildren, Reason reason, String typeParent, String typeChildren, String newValue, String oldValue, String note);

    @Transactional
    List<MaterialValueOrgHistoryDto> saveHistory(List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList);

    List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNullOrByReason(UUID id);

    List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNull();

    List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNullOrByReason(UUID id);

    List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNull();

    List<MaterialValueOrgHistoryDto> getAllHistoryByReasonId(UUID id);

    List<MaterialValueOrgHistoryDto> deleteReasonInHistory(List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList);
}
