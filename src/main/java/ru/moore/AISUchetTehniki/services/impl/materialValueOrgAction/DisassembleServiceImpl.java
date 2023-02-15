package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.DisassembleDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.DisassembleService;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DisassembleServiceImpl implements DisassembleService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final BudgetAccountService budgetAccountService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveDisassemble(List<DisassembleDto> disassembleDtoList) {
        try {
            List<DisassembleDto.DisassembleSpecDto> disassembleSpecDtoList = new ArrayList<>();
            for (DisassembleDto dto : disassembleDtoList) {
                disassembleSpecDtoList.addAll(dto.getSpecification());
            }

            for (int i = 0; i < disassembleSpecDtoList.size(); i++) {
                for (int j = 0; j < disassembleSpecDtoList.size(); j++) {
                    if (i != j) {
                        if (disassembleSpecDtoList.get(i).getId().equals(disassembleSpecDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }

            List<MaterialValueOrg> returnDisassembleList = new ArrayList<>();
            for (DisassembleDto disassembleDto : disassembleDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                Location location = locationService.findById(disassembleDto.getLocationId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ BUDGET_ACCOUNT ПО ID
                BudgetAccount budgetAccount = budgetAccountService.findById(disassembleDto.getBudgetAccountId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonStatement = reasonService.findById(disassembleDto.getStatementId()).orElse(null);

                for (DisassembleDto.DisassembleSpecDto disassembleSpecDto : disassembleDto.getSpecification()) {
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ NAME_IN_ORG ПО ID
                    MaterialValueOrg materialValueOrg = materialValueOrgService.findById(disassembleSpecDto.getId()).orElse(null);
                    if (materialValueOrg.getChildren().size() == 0) {
                        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Нет дочерних записи!");
                    }

                    materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrg, materialValueOrg.getChildren(), reasonStatement, HistoryTypeEnum.DISASSEMBLE.name(), HistoryTypeEnum.DISASSEMBLE_OUT.name(), null, null, null);

                    for (MaterialValueOrg materialValueOrgChildren : materialValueOrg.getChildren()) {
                        materialValueOrgChildren.setLocation(location);
                        materialValueOrgChildren.setResponsible(materialValueOrg.getResponsible());
                        materialValueOrgChildren.setBudgetAccount(budgetAccount);
                        materialValueOrgChildren.setParent(null);

                        materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrgChildren, null, null, HistoryTypeEnum.LOCATION.name(), null, location.getId().toString(), null, null);
                        materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrgChildren, null, null, HistoryTypeEnum.BUDGET_ACCOUNT.name(), null, budgetAccount.getId().toString(), null, null);
                        if (materialValueOrg.getResponsible() != null) {
                            materialValueOrgHistoryService.saveFromStorageIncome(materialValueOrgChildren, null, null, HistoryTypeEnum.RESPONSIBLE.name(), null, materialValueOrg.getResponsible().getId().toString(), null, null);
                        }

                        returnDisassembleList.add(materialValueOrgChildren);
                    }

                    materialValueOrg.setStatus(RegistryStatusEnum.DISASSEMBLE.name());
                    materialValueOrgRepository.save(materialValueOrg);
                }

            }

            materialValueOrgRepository.saveAll(returnDisassembleList);

            return mapperUtils.mapAll(returnDisassembleList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
