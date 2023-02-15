package ru.moore.AISUchetTehniki.services.impl.materialValueOrgAction;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.DisposeOfDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.DisposeOfService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DisposeOfServiceImpl implements DisposeOfService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final MaterialValueOrgService materialValueOrgService;
    private final MaterialValueOrgHistoryService materialValueOrgHistoryService;
    private final ReasonService reasonService;
    private final MapperUtils mapperUtils;

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveDisposeOf(List<DisposeOfDto> disposeOfDtoList) {
        try {
            List<DisposeOfDto.WroteOfDto> addDeviceSpecificationDtoList = new ArrayList<>();
            for (DisposeOfDto disposeOfDto : disposeOfDtoList) {
                addDeviceSpecificationDtoList.addAll(disposeOfDto.getSpecification());
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
            for (DisposeOfDto addDeviceDto : disposeOfDtoList) {
                for (DisposeOfDto.WroteOfDto wroteOfDto : addDeviceDto.getSpecification()) {
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ STORAGE ПО ID
                    MaterialValueOrg storage = materialValueOrgService.findById(wroteOfDto.getId()).orElse(null);
                    //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
                    Reason reasonContract = reasonService.findById(addDeviceDto.getContractId()).orElse(null);

                    storage.setStatus(RegistryStatusEnum.DISPOSE_OF.name());
                    materialValueOrgRepository.save(storage);
                    materialValueOrgHistoryService.saveFromStorageIncome(storage, null, reasonContract, HistoryTypeEnum.DISPOSE_OF.name(), null, null, null, null);
                    materialValueOrgList.add(storage);

                }
            }
            return mapperUtils.mapAll(materialValueOrgList, MaterialValueOrgDto.class);
        } catch (DataAccessException ex) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, Objects.requireNonNull(ex.getRootCause()).getMessage());
        }
    }

}
