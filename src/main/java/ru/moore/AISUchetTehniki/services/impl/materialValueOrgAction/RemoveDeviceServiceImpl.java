package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RemoveDeviceDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.RemoveDeviceService;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RemoveDeviceServiceImpl implements RemoveDeviceService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final BudgetAccountService budgetAccountService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveRemoveDevice(List<RemoveDeviceDto> removeDeviceDtoList) {
        try {
            List<RemoveDeviceDto.RemoveDeviceSpecDto> removeSpecificationDtoList = new ArrayList<>();
            for (int i = 0; i < removeDeviceDtoList.size(); i++) {
                for (int j = 0; j < removeDeviceDtoList.get(i).getSpecification().size(); j++) {
                    removeSpecificationDtoList.add(removeDeviceDtoList.get(i).getSpecification().get(j));
                }
            }

            for (int i = 0; i < removeSpecificationDtoList.size(); i++) {
                for (int j = 0; j < removeSpecificationDtoList.size(); j++) {
                    if (i != j) {
                        if (removeSpecificationDtoList.get(i).getId().equals(removeSpecificationDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }
            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (RemoveDeviceDto removeDeviceDto : removeDeviceDtoList) {
                for (RemoveDeviceDto.RemoveDeviceSpecDto removeDeviceSpecDto : removeDeviceDto.getSpecification()) {
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
                    MaterialValueOrg inDevice = materialValueOrgService.findById(removeDeviceSpecDto.getId()).orElse(null);
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                    Location location = locationService.findById(removeDeviceDto.getLocationId()).orElse(null);
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
                    BudgetAccount budgetAccount = budgetAccountService.findById(removeDeviceDto.getBudgetAccountId()).orElse(null);
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                    Reason reasonStatement = reasonService.findById(removeDeviceDto.getStatementId()).orElse(null);

                    materialValueOrgHistoryService.saveFromStorageIncome(inDevice, Collections.singletonList(inDevice.getParent()), reasonStatement, HistoryTypeEnum.REMOVE_DEVICE.name(), HistoryTypeEnum.REMOVE_DEVICE_OUT.name(), null, null, null);
                    materialValueOrgHistoryService.saveFromStorageIncome(inDevice, null, null, HistoryTypeEnum.LOCATION.name(), null, location.getId().toString(), null, null);
                    materialValueOrgHistoryService.saveFromStorageIncome(inDevice, null, null, HistoryTypeEnum.BUDGET_ACCOUNT.name(), null, budgetAccount.getId().toString(), null, null);


                    inDevice.setParent(null);
                    inDevice.setLocation(location);
                    inDevice.setBudgetAccount(budgetAccount);
                    materialValueOrgRepository.save(inDevice);
                    returnMaterialValueOrgList.add(inDevice);
                }
            }
            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);

        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
