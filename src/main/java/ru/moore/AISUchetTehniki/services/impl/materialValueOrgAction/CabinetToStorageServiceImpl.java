package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.CabinetToStorageDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;
import ru.moore.AISUchetTehniki.services.spr.LocationService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CabinetToStorageServiceImpl {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Transactional
    public List<MaterialValueOrgDto> saveCabinetToStorage(List<CabinetToStorageDto> cabinetToStorageDtoList) {
        for (int i = 0; i < cabinetToStorageDtoList.size(); i++) {
            for (int j = 0; j < cabinetToStorageDtoList.size(); j++) {
                if (i != j) {
                    if (cabinetToStorageDtoList.get(i).getMaterialValueOrgId().equals(cabinetToStorageDtoList.get(j).getMaterialValueOrgId())) {
                        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                    }
                }
            }
        }

        List<MaterialValueOrgDto> materialValueOrgDtoDtoList = new ArrayList<>();
        for (CabinetToStorageDto storageToCabinetDto : cabinetToStorageDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
            MaterialValueOrg materialValueOrg = materialValueOrgService.findById(storageToCabinetDto.getMaterialValueOrgId()).orElse(null);
            if (materialValueOrg.getParent() != null) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "У материальной ценности с ID " + storageToCabinetDto.getMaterialValueOrgId() + " есть родитель!");
            }
            if (!materialValueOrg.getLocation().getType().equals(LocationTypeEnum.CABINET.getName())) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Материальная ценность с ID " + storageToCabinetDto.getMaterialValueOrgId() + " находится не в кабинете!");
            }
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = locationService.findById(storageToCabinetDto.getLocationId()).orElse(null);
            if (!location.getType().equals(LocationTypeEnum.STORAGE.getName())) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Склад с ID " + storageToCabinetDto.getLocationId() + " не является складом!");
            }
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
            Reason contract = reasonService.findById(storageToCabinetDto.getStatementId()).orElse(null);

            materialValueOrg.setLocation(location);
            materialValueOrg.setInvNumber(materialValueOrg.getInvNumber());

            MaterialValueOrg saveMaterialValueOrg = materialValueOrgRepository.save(materialValueOrg);
//                materialValueOrgHistoryService.saveFromStorageStorageToRegistry(materialValueOrg, storageToCabinetDto.getStatementId());

            materialValueOrgDtoDtoList.add(mapperUtils.map(saveMaterialValueOrg, MaterialValueOrgDto.class));
        }
        return materialValueOrgDtoDtoList;
    }
}
