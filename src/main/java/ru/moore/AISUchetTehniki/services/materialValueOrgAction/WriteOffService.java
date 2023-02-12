package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.WriteOffDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;
import ru.moore.AISUchetTehniki.services.ReasonService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WriteOffService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Transactional
    public List<MaterialValueOrgDto> saveWriteOff(List<WriteOffDto> writeOffDto) {
        try {
            List<WriteOffDto.WroteOfDto> addDeviceSpecificationDtoList = new ArrayList<>();
            for (int i = 0; i < writeOffDto.size(); i++) {
                for (int j = 0; j < writeOffDto.get(i).getSpecification().size(); j++) {
                    addDeviceSpecificationDtoList.add(writeOffDto.get(i).getSpecification().get(j));
                }
            }

            for (int i = 0; i < addDeviceSpecificationDtoList.size(); i++) {
                for (int j = 0; j < addDeviceSpecificationDtoList.size(); j++) {
                    if (i != j) {
                        if (addDeviceSpecificationDtoList.get(i).getId().equals(addDeviceSpecificationDtoList.get(j).getId())) {
                            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Повторяющиеся записи!");
                        }
                    }
                }
            }

            List<MaterialValueOrg> materialValueOrgList = new ArrayList<>();
            for (WriteOffDto addDeviceDto : writeOffDto) {
                for (WriteOffDto.WroteOfDto wroteOfDto : addDeviceDto.getSpecification()) {
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                    MaterialValueOrg storage = materialValueOrgService.findById(wroteOfDto.getId()).orElse(null);
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                    Reason reasonStatement = reasonService.findById(addDeviceDto.getStatementId()).orElse(null);

                    storage.setStatus(RegistryStatusEnum.WRITE_OFF.name());
                    materialValueOrgRepository.save(storage);
                    materialValueOrgHistoryService.saveFromStorageIncome(storage, null, reasonStatement, HistoryTypeEnum.WRITE_OFF.name(), null, null, null, null);
                    materialValueOrgList.add(storage);
                }
            }
            return mapperUtils.mapAll(materialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
