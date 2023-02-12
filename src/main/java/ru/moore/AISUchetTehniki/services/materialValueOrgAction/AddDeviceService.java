package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.AddDeviceDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;
import ru.moore.AISUchetTehniki.services.ReasonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AddDeviceService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Transactional
    public List<MaterialValueOrgDto> saveAddDevice(List<AddDeviceDto> addDeviceDtoList) {
        try {
            List<AddDeviceDto.AddDeviceSpecDto> addDeviceSpecDtoList = new ArrayList<>();
            for (int i = 0; i < addDeviceDtoList.size(); i++) {
                for (int j = 0; j < addDeviceDtoList.get(i).getSpecification().size(); j++) {
                    addDeviceSpecDtoList.add(addDeviceDtoList.get(i).getSpecification().get(j));
                }
            }

            for (int i = 0; i < addDeviceSpecDtoList.size(); i++) {
                for (int j = 0; j < addDeviceSpecDtoList.size(); j++) {
                    if (i != j) {
                        if (addDeviceSpecDtoList.get(i).getId().equals(addDeviceSpecDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }

            for (int i = 0; i < addDeviceDtoList.size(); i++) {
                for (int j = 0; j < addDeviceSpecDtoList.size(); j++) {
                        if (addDeviceDtoList.get(i).getInDeviceId().equals(addDeviceSpecDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Добавление в самого себя!");
                        }
                }
            }

            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (AddDeviceDto addDeviceDto : addDeviceDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
                MaterialValueOrg inDevice = materialValueOrgService.findById(addDeviceDto.getInDeviceId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonStatement = reasonService.findById(addDeviceDto.getStatementId()).orElse(null);

                inDevice.setChildren(saveChildren(addDeviceDto.getSpecification(), inDevice));
                materialValueOrgRepository.save(inDevice);

                materialValueOrgHistoryService.saveFromStorageIncome(inDevice, inDevice.getChildren(), reasonStatement, HistoryTypeEnum.ADD_DEVICE.name(), HistoryTypeEnum.ADD_DEVICE_IN.name(), null, null, null);

                returnMaterialValueOrgList.add(inDevice);
            }
            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

    private List<MaterialValueOrg> saveChildren(List<AddDeviceDto.AddDeviceSpecDto> addDeviceSpecDtoList, MaterialValueOrg parent) {
        List<MaterialValueOrg> saveStorageChildrenList = new ArrayList<>();
        if (addDeviceSpecDtoList == null) {
            return null;
        }
        for (AddDeviceDto.AddDeviceSpecDto addDeviceSpecDto : addDeviceSpecDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
            MaterialValueOrg materialValueOrg = materialValueOrgService.findById(addDeviceSpecDto.getId()).orElse(null);
            materialValueOrg.setLocation(null);
            materialValueOrg.setParent(parent);
            saveStorageChildrenList.add(materialValueOrgRepository.save(materialValueOrg));
        }
        return saveStorageChildrenList;
    }

}
