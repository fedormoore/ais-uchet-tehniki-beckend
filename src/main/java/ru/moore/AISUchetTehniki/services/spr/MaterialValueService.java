package ru.moore.AISUchetTehniki.services.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.spr.MaterialValueDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValueType;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.repositories.spr.MaterialValueRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialValueService {

    private final MaterialValueRepository materialValueRepository;
    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueTypeService materialValueTypeService;
    private final MapperUtils mapperUtils;

    public Page<MaterialValueDto> getAllMaterialValuePage(Specification<MaterialValue> spec, int page, int pageSize) {
        return materialValueRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "nameFirm"))).map(model -> toDtoMaterialValue(model));
    }

    public List<MaterialValueDto> getAllMaterialValueList() {
        return mapperUtils.mapAll(materialValueRepository.findAll(), MaterialValueDto.class);
    }

    @Transactional
    public List<MaterialValueDto> saveMaterialValue(List<MaterialValueDto> materialValueDtoList) {
        List<MaterialValue> returnMaterialValueList = new ArrayList<>();
        for (MaterialValueDto materialValueDto : materialValueDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MATERIAL_VALUE ПО ID
            MaterialValue materialValue = findById(materialValueDto.getId()).orElse(new MaterialValue());
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MATERIAL_VALUE_TYPE ПО ID
            MaterialValueType materialValueType = materialValueTypeService.findById(materialValueDto.getMaterialValueTypeId()).orElse(new MaterialValueType());

            materialValue.setMaterialValueType(materialValueType);
            materialValue.setNameInOrg(materialValueDto.getNameInOrg());
            materialValue.setNameFirm(materialValueDto.getNameFirm());
            materialValue.setNameModel(materialValueDto.getNameModel());

            materialValueRepository.save(materialValue);
            returnMaterialValueList.add(materialValue);
        }
        return mapperUtils.mapAll(returnMaterialValueList, MaterialValueDto.class);
    }

    private MaterialValueDto toDtoMaterialValue(MaterialValue materialValue) {
        return mapperUtils.map(materialValue, MaterialValueDto.class);
    }

    public List<MaterialValueDto> getAllMaterialValueByAddToOtherTrue() {
        return mapperUtils.mapAll(materialValueRepository.findAllByMaterialValueTypeAddToOther(true), MaterialValueDto.class);
    }

    public List<MaterialValueDto> getAllMaterialValueByNameInOrgIsNotNull() {
        return mapperUtils.mapAll(materialValueRepository.findAllByNameInOrgIsNotNull(), MaterialValueDto.class);
    }

    public Optional<MaterialValue> findById(UUID id) {
        if (id != null) {
            Optional<MaterialValue> modelFind = materialValueRepository.findById(id);
            if (modelFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Оборудование с ID " + id + " не найдена!");
            }
            return modelFind;
        } else {
            return Optional.empty();
        }
    }

    public List<String> getAllFirmList() {
        List<String> returnStringList = new ArrayList<>();
        for (String device : materialValueRepository.findAllFirm()) {
            returnStringList.add(device);
        }
        return returnStringList;
    }

    @Transactional
    public ResponseEntity<?> deleteMaterialValue(List<MaterialValueDto> materialValueDtoList) {
        for (MaterialValueDto materialValueDto : materialValueDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MATERIAL_VALUE ПО ID
            MaterialValue materialValue = findById(materialValueDto.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByMaterialValueId(materialValue.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            materialValue.setDeleted(true);
            materialValueRepository.save(materialValue);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
