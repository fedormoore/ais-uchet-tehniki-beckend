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
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValueType;
import ru.moore.AISUchetTehniki.repositories.spr.MaterialValueRepository;
import ru.moore.AISUchetTehniki.repositories.spr.MaterialValueTypeRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialValueTypeService {

    private final MaterialValueTypeRepository materialValueTypeRepository;
    private final MaterialValueRepository materialValueRepository;
    private final MapperUtils mapperUtils;

    public Page<MaterialValueTypeDto> getAllMaterialValueTypePage(Specification<MaterialValueType> spec, int page, int pageSize) {
        return materialValueTypeRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "name"))).map(deviceType -> toDtoMaterialValueType(deviceType));
    }

    public List<MaterialValueTypeDto> getAllMaterialValueTypeList() {
        return mapperUtils.mapAll(materialValueTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "name")), MaterialValueTypeDto.class);
    }

    @Transactional
    public List<MaterialValueTypeDto> saveMaterialValueType(List<MaterialValueTypeDto> materialValueTypeDtoList) {
        List<MaterialValueType> returnMaterialValueType = new ArrayList<>();
        for (MaterialValueTypeDto materialValueTypeDto : materialValueTypeDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MATERIAL_VALUE_TYPE ПО ID
            MaterialValueType materialValueType = findById(materialValueTypeDto.getId()).orElse(new MaterialValueType());

            materialValueType.setName(materialValueTypeDto.getName());
            materialValueType.setAddToOther(materialValueTypeDto.isAddToOther());
            materialValueType.setAddOther(materialValueTypeDto.isAddOther());

            materialValueTypeRepository.save(materialValueType);
            returnMaterialValueType.add(materialValueType);
        }
        return mapperUtils.mapAll(returnMaterialValueType, MaterialValueTypeDto.class);
    }

    private MaterialValueTypeDto toDtoMaterialValueType(MaterialValueType materialValueType) {
        return mapperUtils.map(materialValueType, MaterialValueTypeDto.class);
    }

    public Optional<MaterialValueType> findById(UUID id) {
        if (id != null) {
            Optional<MaterialValueType> userDeviceType = materialValueTypeRepository.findById(id);
            if (userDeviceType.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись не найдена!");
            }
            return userDeviceType;
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public ResponseEntity<?> deleteMaterialValueType(List<MaterialValueTypeDto> materialValueTypeDtoList) {
        for (MaterialValueTypeDto materialValueTypeDto : materialValueTypeDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MATERIAL_VALUE_TYPE ПО ID
            MaterialValueType materialValueType = findById(materialValueTypeDto.getId()).orElse(null);

            List<MaterialValue> materialValue = materialValueRepository.findByMaterialValueTypeId(materialValueType.getId());
            if (materialValue.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            materialValueType.setDeleted(true);
            materialValueTypeRepository.save(materialValueType);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
