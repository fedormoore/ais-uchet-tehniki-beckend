package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.IncomeDto;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.models.Entity.IndexB;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.*;
import ru.moore.AISUchetTehniki.repositories.IndexBRepository;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.security.UserPrincipal;
import ru.moore.AISUchetTehniki.services.AccountService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.spr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final MaterialValueOrgRepository materialValueOrgRepository;

    private final IndexBRepository indexBRepository;
    private final AccountService accountService;
    private final MaterialValueService materialValueService;
    private final LocationService locationService;
    private final UserService userService;
    private final BudgetAccountService budgetAccountService;
    private final OrganizationService organizationService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    String barcodeAccount = null;
    String barcodeDevice = null;
    String barcodeAddDevice = null;

    @Transactional
    public List<MaterialValueOrgDto> saveIncome(IncomeDto incomeDto, Authentication authentication) {
        try {
            IndexB indexB = indexBRepository.findIndexB();
            barcodeAddDevice = String.valueOf(indexB.getIndexB());

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            Account account = accountService.findById(userPrincipal.getId()).orElse(null);
            barcodeAccount = String.valueOf(account.getIndexB());

            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
            Organization organization = organizationService.findById(incomeDto.getOrganizationId()).orElse(null);
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
            Reason reasonContract = reasonService.findById(incomeDto.getContractId()).orElse(null);
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = locationService.findById(incomeDto.getLocationId()).orElse(null);
            if (location.getType() != LocationTypeEnum.STORAGE.getName()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Расположение не является складом!");
            }
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ USER ПО ID
            User responsible = userService.findById(incomeDto.getResponsibleId()).orElse(null);

            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (IncomeDto.IncomeSpecDto incomeSpecDto : incomeDto.getSpec()) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ MaterialValue ПО ID
                MaterialValue materialValue = materialValueService.findById(incomeSpecDto.getMaterialValueId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
                BudgetAccount budgetAccount = budgetAccountService.findById(incomeSpecDto.getBudgetAccountId()).orElse(null);

                MaterialValueOrg materialValueOrg = new MaterialValueOrg();
                materialValueOrg.setBarcode(generateBarcode(String.valueOf(materialValue.getIndexB())));
                if (materialValue.getMaterialValueType().getName().equals("Картридж")) {
                    materialValueOrg.setStatus(RegistryStatusEnum.CARTRIDGE_NEW.name());
                }
                materialValueOrg.setMaterialValue(materialValue);
                materialValueOrg.setSum(incomeSpecDto.getSum());
                materialValueOrg.setLocation(location);
                materialValueOrg.setResponsible(responsible);
                materialValueOrg.setBudgetAccount(budgetAccount);
                materialValueOrg.setOrganization(organization);
                materialValueOrg.setChildren(saveChildren(incomeSpecDto.getChildren(), materialValueOrg));

                materialValueOrgRepository.save(materialValueOrg);

                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, materialValueOrg.getChildren(), reasonContract, HistoryTypeEnum.INCOME.name(), HistoryTypeEnum.INCOME.name(),null, null, null);
                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.LOCATION.name(), null, materialValueOrg.getLocation().getId().toString(), null, null);
                materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.BUDGET_ACCOUNT.name(), null, materialValueOrg.getBudgetAccount().getId().toString(), null, null);
                if (materialValueOrg.getResponsible() != null) {
                    materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.RESPONSIBLE.name(), null, responsible.getId().toString(), null, null);
                }
                if (materialValueOrg.getOrganization() != null) {
                    materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, null, null, HistoryTypeEnum.ORGANIZATION.name(), null, materialValueOrg.getOrganization().getId().toString(), null, null);
                }
                returnMaterialValueOrgList.add(materialValueOrg);
            }
            indexB.setIndexB(Integer.valueOf(barcodeAddDevice));
            indexBRepository.save(indexB);
            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

    private List<MaterialValueOrg> saveChildren(List<IncomeDto.IncomeSpecDto.IncomeChildrenDto> incomeChildrenDtoList, MaterialValueOrg parent) {
        List<MaterialValueOrg> returnMaterialValueOrgChildrenList = new ArrayList<>();
        if (incomeChildrenDtoList == null) {
            return null;
        }
        for (IncomeDto.IncomeSpecDto.IncomeChildrenDto incomeChildrenDto : incomeChildrenDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ DEVICE ПО ID
            MaterialValue materialValue = materialValueService.findById(incomeChildrenDto.getMaterialValueId()).orElse(null);
            if (!materialValue.getMaterialValueType().isAddToOther()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "МЦ " + materialValue.getMaterialValueType().getName() + " " + materialValue.getNameInOrg() + " " + materialValue.getNameFirm() + " " + materialValue.getNameModel() + " нелзя добавлять в состав других МЦ!");
            }
            if (!parent.getMaterialValue().getMaterialValueType().isAddOther()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "МЦ " + parent.getMaterialValue().getMaterialValueType().getName() + " " + parent.getMaterialValue().getNameInOrg() + " " + parent.getMaterialValue().getNameFirm() + " " + parent.getMaterialValue().getNameModel() + " не может включать в себя другие МЦ!");
            }
            MaterialValueOrg materialValueOrg = new MaterialValueOrg();
            materialValueOrg.setBarcode(generateBarcode(String.valueOf(materialValue.getIndexB())));
            materialValueOrg.setMaterialValue(materialValue);
            materialValueOrg.setSum(incomeChildrenDto.getSum());
            materialValueOrg.setParent(parent);
            materialValueOrg.setChildren(saveChildren(incomeChildrenDto.getChildren(), materialValueOrg));

            returnMaterialValueOrgChildrenList.add(materialValueOrgRepository.save(materialValueOrg));
        }
        return returnMaterialValueOrgChildrenList;
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
}
