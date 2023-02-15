package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.ReplacementDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.ReplacementService;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReplacementServiceImpl implements ReplacementService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final BudgetAccountService budgetAccountService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveReplacement(List<ReplacementDto> replacementDtoList) {
        try {

            for (int i = 0; i < replacementDtoList.size(); i++) {
                for (int j = 0; j < replacementDtoList.size(); j++) {
                    if (i != j) {
                        if (replacementDtoList.get(i).getReplacementInDeviceId().equals(replacementDtoList.get(j).getReplacementInDeviceId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                        if (replacementDtoList.get(i).getReplacementToDeviceId().equals(replacementDtoList.get(j).getReplacementToDeviceId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }
            for (int i = 0; i < replacementDtoList.size(); i++) {
                for (int j = 0; j < replacementDtoList.size(); j++) {
                        if (replacementDtoList.get(i).getReplacementInDeviceId().equals(replacementDtoList.get(j).getReplacementToDeviceId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Замена на самого себя!");
                        }
                }
            }

            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (ReplacementDto disassembleDto : replacementDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                MaterialValueOrg replacementInDevice = materialValueOrgService.findById(disassembleDto.getReplacementInDeviceId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                MaterialValueOrg replacementToDevice = materialValueOrgService.findById(disassembleDto.getReplacementToDeviceId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                Location location = locationService.findById(disassembleDto.getLocationId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
                BudgetAccount budgetAccount = budgetAccountService.findById(disassembleDto.getBudgetAccountId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonStatement = reasonService.findById(disassembleDto.getStatementId()).orElse(null);

                materialValueOrgHistoryService.saveFromStorageIncome(replacementInDevice.getParent(), List.of(replacementInDevice, replacementToDevice), reasonStatement, HistoryTypeEnum.REPLACEMENT.name(), HistoryTypeEnum.REPLACEMENT.name(), null, null, null);

                replacementToDevice.setParent(replacementInDevice.getParent());
                materialValueOrgRepository.save(replacementToDevice);

                replacementInDevice.setParent(null);
                replacementInDevice.setLocation(location);
                replacementInDevice.setBudgetAccount(budgetAccount);
                materialValueOrgRepository.save(replacementInDevice);
            }
            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }

    }

}
