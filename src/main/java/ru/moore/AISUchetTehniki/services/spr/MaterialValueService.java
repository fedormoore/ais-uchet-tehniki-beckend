package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialValueService {
    Page<MaterialValueDto> getAllMaterialValuePage(Specification<MaterialValue> build, int page, int limit);

    List<MaterialValueDto> saveMaterialValueDTOList(List<MaterialValueDto> materialValueDtoList);
    MaterialValue saveMaterialValue(MaterialValue materialValue);

    ResponseEntity<?> deleteMaterialValue(List<MaterialValueDto> materialValueDtoList);

    List<MaterialValueDto> getAllMaterialValueList();

    List<MaterialValueDto> getAllMaterialValueByNameInOrgIsNotNull();

    List<MaterialValueDto> getAllMaterialValueByAddToOtherTrue();

    List<String> getAllFirmList();

    Optional<MaterialValue> findById(UUID id);
    Optional<MaterialValue> findByNameInOrgAndNameFirmAndNameModel(String nameInOrg, String nameFirm, String nameModel);
}
