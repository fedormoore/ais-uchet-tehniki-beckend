package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RepairDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.RepairService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RepairServiceImpl implements RepairService {

    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveRepair(List<RepairDto> repairDtoList) {
        try {
            for (int i = 0; i < repairDtoList.size(); i++) {
                for (int j = 0; j < repairDtoList.size(); j++) {
                    if (i != j) {
                        if (repairDtoList.get(i).getDeviceId().equals(repairDtoList.get(j).getDeviceId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }
            List<MaterialValueOrgDto> materialValueOrgDtoDtoList = new ArrayList<>();
            for (RepairDto addDeviceDto : repairDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
               MaterialValueOrg device = materialValueOrgService.findById(addDeviceDto.getDeviceId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonContract = reasonService.findById(addDeviceDto.getStatementId()).orElse(null);

                materialValueOrgHistoryService.saveFromStorageIncome(device, null, reasonContract, HistoryTypeEnum.REPAIR.name(), null, null, null, addDeviceDto.getNote());

                materialValueOrgDtoDtoList.add(mapperUtils.map(device, MaterialValueOrgDto.class));
            }
            return materialValueOrgDtoDtoList;
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
