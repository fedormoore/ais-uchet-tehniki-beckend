package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValueType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialValueTypeService {
    Page<MaterialValueTypeDto> getAllMaterialValueTypePage(Specification<MaterialValueType> build, int page, int limit);

    List<MaterialValueTypeDto> saveMaterialValueType(List<MaterialValueTypeDto> materialValueTypeDtoList);

    ResponseEntity<?> deleteMaterialValueType(List<MaterialValueTypeDto> materialValueTypeDtoList);

    List<MaterialValueTypeDto> getAllMaterialValueTypeList();

    Optional<MaterialValueType> findById(UUID id);
}
