package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.StorageToCabinetDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.StorageToCabinetService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.services.spr.UserService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StorageToCabinetServiceImpl implements StorageToCabinetService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final ReasonService reasonServiceImpl;
    private final MaterialValueOrgService materialValueOrgServiceImpl;
    private final LocationService locationServiceImpl;
    private final UserService userServiceImpl;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryServiceImpl;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveStorageToCabinet(List<StorageToCabinetDto> storageToCabinetDtoList) {
        try {
            for (int i = 0; i < storageToCabinetDtoList.size(); i++) {
                for (int j = 0; j < storageToCabinetDtoList.size(); j++) {
                    if (i != j) {
                        if (storageToCabinetDtoList.get(i).getMaterialValueOrgId().equals(storageToCabinetDtoList.get(j).getMaterialValueOrgId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }

            List<MaterialValueOrgDto> returnMaterialValueOrgDtoDtoList = new ArrayList<>();
            for (StorageToCabinetDto storageToCabinetDto : storageToCabinetDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
                MaterialValueOrg materialValueOrg = materialValueOrgServiceImpl.findById(storageToCabinetDto.getMaterialValueOrgId()).orElse(null);
                if (materialValueOrg.getParent() != null) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "У материальной ценности с ID " + storageToCabinetDto.getMaterialValueOrgId() + " есть родитель!");
                }
                if (!materialValueOrg.getLocation().getType().equals(LocationTypeEnum.STORAGE.getName())) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Материальная ценность с ID " + storageToCabinetDto.getMaterialValueOrgId() + " находится не в складе!");
                }
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                Location location = locationServiceImpl.findById(storageToCabinetDto.getLocationId()).orElse(null);
                if (!location.getType().equals(LocationTypeEnum.CABINET.getName())) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Кабинет с ID " + storageToCabinetDto.getLocationId() + " не является кабинетом!");
                }
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ USER ПО ID
                User user = userServiceImpl.findById(storageToCabinetDto.getUserId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ REASON ПО ID
                Reason reasonStatement = reasonServiceImpl.findById(storageToCabinetDto.getStatementId()).orElse(null);

                materialValueOrg.setLocation(location);
                materialValueOrg.setUser(user);
                materialValueOrg.setInvNumber(storageToCabinetDto.getInvNumber());

                materialValueOrgRepository.save(materialValueOrg);

                materialValueOrgHistoryServiceImpl.saveFromStorageIncome(materialValueOrg, null, reasonStatement, HistoryTypeEnum.STORAGE_TO_REGISTRY.name(), null, null, null, null);
                materialValueOrgHistoryServiceImpl.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.LOCATION.name(), null, location.getId().toString(), null, null);
                if (materialValueOrg.getInvNumber() != null) {
                    materialValueOrgHistoryServiceImpl.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.INV_NUMBER.name(), null, materialValueOrg.getInvNumber(), null, null);
                }
                if (materialValueOrg.getUser() != null) {
                    materialValueOrgHistoryServiceImpl.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.USER.name(), null, user.getId().toString(), null, null);
                }
                returnMaterialValueOrgDtoDtoList.add(mapperUtils.map(materialValueOrg, MaterialValueOrgDto.class));
            }

            return returnMaterialValueOrgDtoDtoList;
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }
}
