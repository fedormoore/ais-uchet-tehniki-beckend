package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RefillingCartridgeDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RemoveDeviceDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefillingCartridgeService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Transactional
    public List<MaterialValueOrgDto> saveRefillingCartridge(List<RefillingCartridgeDto> refillingCartridgeDtoList) {
        try {
            List<RefillingCartridgeDto.RefillingCartridgeListDto> refillingCartridgeListDto = new ArrayList<>();
            for (int i = 0; i < refillingCartridgeDtoList.size(); i++) {
                for (int j = 0; j < refillingCartridgeDtoList.get(i).getCartridge().size(); j++) {
                    refillingCartridgeListDto.add(refillingCartridgeDtoList.get(i).getCartridge().get(j));
                }
            }

            for (int i = 0; i < refillingCartridgeListDto.size(); i++) {
                for (int j = 0; j < refillingCartridgeListDto.size(); j++) {
                    if (i != j) {
                        if (refillingCartridgeListDto.get(i).getId().equals(refillingCartridgeListDto.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }
            List<MaterialValueOrg> returnMaterialValueOrgList = new ArrayList<>();
            for (RefillingCartridgeDto addDeviceDto : refillingCartridgeDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonContract = reasonService.findById(addDeviceDto.getContractId()).orElse(null);
                for (RefillingCartridgeDto.RefillingCartridgeListDto refillingCartridgeDto : addDeviceDto.getCartridge()) {
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                    MaterialValueOrg cartridge = materialValueOrgService.findById(refillingCartridgeDto.getId()).orElse(null);

                    materialValueOrgHistoryService.saveFromStorageIncome(cartridge, null, reasonContract, HistoryTypeEnum.CARTRIDGE_REFILL.name(), null, null, null, null);

                    cartridge.setStatus(RegistryStatusEnum.CARTRIDGE_REFILL.name());
                    materialValueOrgRepository.save(cartridge);
                    returnMaterialValueOrgList.add(cartridge);
                }
            }
            return mapperUtils.mapAll(returnMaterialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
