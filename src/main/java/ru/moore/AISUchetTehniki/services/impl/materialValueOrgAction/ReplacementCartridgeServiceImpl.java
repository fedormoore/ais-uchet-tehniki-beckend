package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.ReplacementCartridgeDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.ReplacementCartridgeService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReplacementCartridgeServiceImpl implements ReplacementCartridgeService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final LocationService locationService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveReplacementCartridge(List<ReplacementCartridgeDto> replacementCartridgeDtoList) {
        try {
            for (int i = 0; i < replacementCartridgeDtoList.size(); i++) {
                for (int j = 0; j < replacementCartridgeDtoList.size(); j++) {
                    if (i != j) {
                        if (replacementCartridgeDtoList.get(i).getCartridgeId().equals(replacementCartridgeDtoList.get(j).getCartridgeId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                        if (replacementCartridgeDtoList.get(i).getPrinterId().equals(replacementCartridgeDtoList.get(j).getPrinterId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }
            List<MaterialValueOrgDto> materialValueOrgDtoDtoList = new ArrayList<>();
            for (ReplacementCartridgeDto addDeviceDto : replacementCartridgeDtoList) {
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                MaterialValueOrg printer = materialValueOrgService.findById(addDeviceDto.getPrinterId()).orElse(null);
                MaterialValueOrg cartridge = materialValueOrgService.findById(addDeviceDto.getCartridgeId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
                Location location = locationService.findById(addDeviceDto.getLocationId()).orElse(null);
                //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                Reason reasonStatement = reasonService.findById(addDeviceDto.getStatementId()).orElse(null);

                if (printer.getChildren().size() > 0) {
                    materialValueOrgHistoryService.saveFromStorageIncome(printer, List.of(cartridge, printer.getChildren().get(0)), reasonStatement, HistoryTypeEnum.CARTRIDGE_PRINTER.name(), HistoryTypeEnum.CARTRIDGE_PRINTER.name(), null, null, null);
                } else {
                    materialValueOrgHistoryService.saveFromStorageIncome(printer, List.of(cartridge), reasonStatement, HistoryTypeEnum.CARTRIDGE_PRINTER.name(), HistoryTypeEnum.CARTRIDGE_PRINTER.name(), null, null, null);
                }

                if (printer.getChildren().size() > 0) {
                    MaterialValueOrg outCartridge = printer.getChildren().get(0);
                    outCartridge.setParent(null);
                    outCartridge.setLocation(location);
                    outCartridge.setStatus(RegistryStatusEnum.CARTRIDGE_NEEDS_REFILLING.name());
                    materialValueOrgRepository.save(outCartridge);
//                    materialValueOrgHistoryService.saveFromStorageIncome(outCartridge, Collections.singletonList(printer), reasonStatement.getId(), HistoryTypeEnum.CARTRIDGE_OUT.name(), HistoryTypeEnum.CARTRIDGE_PRINTER.name(), null, null, null);
                }

                cartridge.setParent(printer);
                cartridge.setLocation(null);
                cartridge.setStatus(RegistryStatusEnum.CARTRIDGE_IN_PRINTER.name());
                materialValueOrgRepository.save(cartridge);
            }
            return materialValueOrgDtoDtoList;
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
