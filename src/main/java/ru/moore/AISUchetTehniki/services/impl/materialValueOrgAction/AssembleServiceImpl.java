package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.AssembleDto;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.models.Entity.IndexB;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.IndexBRepository;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.security.UserPrincipal;
import ru.moore.AISUchetTehniki.services.AccountService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.services.spr.MaterialValueService;
import ru.moore.AISUchetTehniki.services.spr.UserService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AssembleServiceImpl {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final IndexBRepository indexBRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueService materialValueService;
    private final LocationService locationService;
    private final UserService userService;
    private final BudgetAccountService budgetAccountService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final AccountService accountService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    String barcodeAccount = null;
    String barcodeDevice = null;
    String barcodeAddDevice = null;

    @Transactional
    public List<MaterialValueOrgDto> saveAssemble(List<AssembleDto> assembleDtoList, Authentication authentication) {
        try {
            List<AssembleDto.AssembleSpecDto> assembleSpecDtoList = new ArrayList<>();
            for (int i = 0; i < assembleDtoList.size(); i++) {
                for (int j = 0; j < assembleDtoList.get(i).getSpecification().size(); j++) {
                    assembleSpecDtoList.add(assembleDtoList.get(i).getSpecification().get(j));
                }
            }

            for (int i = 0; i < assembleSpecDtoList.size(); i++) {
                for (int j = 0; j < assembleSpecDtoList.size(); j++) {
                    if (i != j) {
                        if (assembleSpecDtoList.get(i).getId().equals(assembleSpecDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }

            IndexB indexB = indexBRepository.findIndexB();
            barcodeAddDevice = String.valueOf(indexB.getIndexB());

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Account account = accountService.findById(userPrincipal.getId()).orElse(null);
            barcodeAccount = String.valueOf(account.getIndexB());

            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (AssembleDto assembleDto : assembleDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
                MaterialValue materialValue = materialValueService.findById(assembleDto.getMaterialValueId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                Location location = locationService.findById(assembleDto.getLocationId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ USER ПО ID
                User responsible = userService.findById(assembleDto.getResponsibleId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
                BudgetAccount budgetAccount = budgetAccountService.findById(assembleDto.getBudgetAccountId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonStatement = reasonService.findById(assembleDto.getStatementId()).orElse(null);

                MaterialValueOrg materialValueOrg = new MaterialValueOrg();
                materialValueOrg.setBarcode(generateBarcode(String.valueOf(materialValue.getIndexB())));
                materialValueOrg.setMaterialValue(materialValue);
                materialValueOrg.setLocation(location);
                materialValueOrg.setResponsible(responsible);
                materialValueOrg.setBudgetAccount(budgetAccount);
                materialValueOrg.setChildren(saveChildren(assembleDto.getSpecification(), materialValueOrg));
                materialValueOrgRepository.save(materialValueOrg);

                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, materialValueOrg.getChildren(), reasonStatement, HistoryTypeEnum.ASSEMBLE.name(), HistoryTypeEnum.ASSEMBLE_IN.name(), null, null, null);
                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.LOCATION.name(), null, location.getId().toString(), null, null);
                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.BUDGET_ACCOUNT.name(), null, budgetAccount.getId().toString(), null, null);
                if (materialValueOrg.getResponsible() != null) {
                    materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.RESPONSIBLE.name(), null, materialValueOrg.getResponsible().getId().toString(), null, null);
                }
                if (materialValueOrg.getOrganization() != null) {
                    materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.ORGANIZATION.name(), null, materialValueOrg.getOrganization().getId().toString(), null, null);
                }

                returnMaterialValueOrgList.add(materialValueOrg);
            }

            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

    private String generateBarcode(String materialValueIndexB) {
        barcodeAddDevice = String.valueOf(Integer.valueOf(barcodeAddDevice) + 1);
        if (barcodeAddDevice.length() >= 3) {
            barcodeAddDevice = "0" + barcodeAddDevice;
        }
        while (barcodeAddDevice.length() < 3) {
            barcodeAddDevice = "0" + barcodeAddDevice;
        }

        barcodeDevice = materialValueIndexB;
        if (barcodeDevice.length() > 3) {
            barcodeDevice = "0" + barcodeDevice;
        }
        while (barcodeDevice.length() < 3) {
            barcodeDevice = "0" + barcodeDevice;
        }
        return barcodeAccount + barcodeDevice + barcodeAddDevice;
    }

    private List<MaterialValueOrg> saveChildren(List<AssembleDto.AssembleSpecDto> assembleSpecDtoList, ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg parent) {
        if (assembleSpecDtoList == null) {
            return null;
        }
        List<MaterialValueOrg> returnAssembleChildrenList = new ArrayList<>();
        for (AssembleDto.AssembleSpecDto assembleSpecDto : assembleSpecDtoList) {
            MaterialValueOrg materialValueOrg = materialValueOrgService.findById(assembleSpecDto.getId()).orElse(null);
            if (materialValueOrg.getParent() != null) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "У материальной ценности с ID " + assembleSpecDto.getId() + " есть родитель!");
            }
            if (!materialValueOrg.getLocation().getType().equals(LocationTypeEnum.STORAGE.getName())) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Материальная ценность с ID " + assembleSpecDto.getId() + " находится не в складе!");
            }
            materialValueOrg.setLocation(null);
            materialValueOrg.setResponsible(null);
            materialValueOrg.setBudgetAccount(null);
            materialValueOrg.setParent(parent);
            materialValueOrgRepository.save(materialValueOrg);

            returnAssembleChildrenList.add(materialValueOrg);
        }
        return returnAssembleChildrenList;
    }

}
