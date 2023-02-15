package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.HistoryTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgHistoryDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrgHistory;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgHistoryRepository;
import ru.moore.AISUchetTehniki.repositories.ReasonRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgHistoryService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.services.spr.OrganizationService;
import ru.moore.AISUchetTehniki.services.spr.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialValueOrgHistoryServiceImpl implements MaterialValueOrgHistoryService {

    private final MaterialValueOrgHistoryRepository materialValueOrgHistoryRepository;
    private final ReasonRepository reasonRepository;
    private final LocationService locationService;
    private final UserService userService;
    private final BudgetAccountService budgetAccountService;
    private final OrganizationService organizationService;

    private final MapperUtils mapperUtils;

    @Override
    public List<MaterialValueOrgHistoryDto> getAllHistoryByRegistryId(UUID listMaterialValueIdId) {
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByMaterialValueOrgIdOrderByCreatedAtAsc(listMaterialValueIdId);

        List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList = new ArrayList<>();
        for (MaterialValueOrgHistory materialValueOrgHistory : materialValueOrgHistoryList) {
            MaterialValueOrgHistoryDto materialValueOrgHistoryDto = new MaterialValueOrgHistoryDto();
            materialValueOrgHistoryDto.setId(materialValueOrgHistory.getId());
            materialValueOrgHistoryDto.setDateCreate(materialValueOrgHistory.getCreatedAt());

            MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto materialValueOrgHistoryReasonDto = new MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto();
            if (materialValueOrgHistory.getReason() != null) {
                Reason reason = findReasonById(materialValueOrgHistory.getReason().getId()).orElse(null);
                materialValueOrgHistoryReasonDto.setId(materialValueOrgHistory.getReason().getId());
                materialValueOrgHistoryReasonDto.setDate(reason.getDate());
                materialValueOrgHistoryReasonDto.setNumber(reason.getNumber());
                materialValueOrgHistoryReasonDto.setSum(reason.getSum());
            }
            materialValueOrgHistoryDto.setReason(materialValueOrgHistoryReasonDto);

            materialValueOrgHistoryDto.setType(materialValueOrgHistory.getType());

            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.LOCATION.getName())) {
                materialValueOrgHistoryDto.setNewValue(locationService.findById(UUID.fromString(materialValueOrgHistory.getNewValue())).get().getName());
            }
            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.RESPONSIBLE.getName())) {
                User responsible = userService.findById(UUID.fromString(materialValueOrgHistory.getNewValue())).orElse(null);
                materialValueOrgHistoryDto.setNewValue(responsible.getLastName() + " " + responsible.getFirstName() + " " + responsible.getMiddleNames());
            }
            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.USER.getName())) {
                User user = userService.findById(UUID.fromString(materialValueOrgHistory.getNewValue())).orElse(null);
                materialValueOrgHistoryDto.setNewValue(user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleNames());
            }
            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.BUDGET_ACCOUNT.getName())) {
                materialValueOrgHistoryDto.setNewValue(budgetAccountService.findById(UUID.fromString(materialValueOrgHistory.getNewValue())).get().getName());
            }
            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.ORGANIZATION.getName())) {
                materialValueOrgHistoryDto.setNewValue(organizationService.findById(UUID.fromString(materialValueOrgHistory.getNewValue())).get().getName());
            }
            if (materialValueOrgHistory.getType().equals(HistoryTypeEnum.INV_NUMBER.getName())) {
                materialValueOrgHistoryDto.setNewValue(materialValueOrgHistory.getNewValue());
            }

            materialValueOrgHistoryDto.setParent(getParent(materialValueOrgHistory));
            materialValueOrgHistoryDto.setChildren(getChildren(materialValueOrgHistory));

            materialValueOrgHistoryDto.setNote(materialValueOrgHistory.getNote());

            materialValueOrgHistoryDtoList.add(materialValueOrgHistoryDto);
        }

        return materialValueOrgHistoryDtoList;
    }

    private MaterialValueOrgHistoryDto.DeviceHistoryParentDto getParent(MaterialValueOrgHistory materialValueOrgHistory) {
        if (materialValueOrgHistory.getParent() != null) {
            MaterialValueOrgHistoryDto.DeviceHistoryParentDto deviceHistoryParentDto = new MaterialValueOrgHistoryDto.DeviceHistoryParentDto();
            deviceHistoryParentDto.setId(materialValueOrgHistory.getId());

            deviceHistoryParentDto.setType(materialValueOrgHistory.getType());

            if (materialValueOrgHistory.getParent().getReason() != null) {
                MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto materialValueOrgHistoryReasonDto = new MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto();

                materialValueOrgHistoryReasonDto.setId(materialValueOrgHistory.getParent().getReason().getId());
                materialValueOrgHistoryReasonDto.setDate(materialValueOrgHistory.getParent().getReason().getDate());
                materialValueOrgHistoryReasonDto.setNumber(materialValueOrgHistory.getParent().getReason().getNumber());
                materialValueOrgHistoryReasonDto.setSum(materialValueOrgHistory.getParent().getReason().getSum());

                deviceHistoryParentDto.setReason(materialValueOrgHistoryReasonDto);
            }

            deviceHistoryParentDto.setMaterialValueOrg(mapperUtils.map(materialValueOrgHistory.getParent().getMaterialValueOrg(), MaterialValueOrgDto.class));
            deviceHistoryParentDto.setParent(getParent(materialValueOrgHistory.getParent()));
            return deviceHistoryParentDto;
        } else {
            return null;
        }
    }

    private List<MaterialValueOrgHistoryDto.DeviceHistoryChildrenDto> getChildren(MaterialValueOrgHistory materialValueOrgHistory) {
        if (materialValueOrgHistory.getChildren() != null) {
            List<MaterialValueOrgHistoryDto.DeviceHistoryChildrenDto> deviceHistoryChildrenDtoList = new ArrayList<>();
            for (MaterialValueOrgHistory materialValueOrgHistoryChildren : materialValueOrgHistory.getChildren()) {
                MaterialValueOrgHistoryDto.DeviceHistoryChildrenDto deviceHistoryChildrenDto = new MaterialValueOrgHistoryDto.DeviceHistoryChildrenDto();
                deviceHistoryChildrenDto.setId(materialValueOrgHistoryChildren.getId());

                deviceHistoryChildrenDto.setType(materialValueOrgHistoryChildren.getType());

                if (materialValueOrgHistoryChildren.getReason() != null) {
                    MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto materialValueOrgHistoryReasonDto = new MaterialValueOrgHistoryDto.MaterialValueOrgHistoryReasonDto();
                    materialValueOrgHistoryReasonDto.setId(materialValueOrgHistoryChildren.getReason().getId());
                    materialValueOrgHistoryReasonDto.setDate(materialValueOrgHistoryChildren.getReason().getDate());
                    materialValueOrgHistoryReasonDto.setNumber(materialValueOrgHistoryChildren.getReason().getNumber());
                    materialValueOrgHistoryReasonDto.setSum(materialValueOrgHistoryChildren.getReason().getSum());
                    deviceHistoryChildrenDto.setReason(materialValueOrgHistoryReasonDto);
                }

                deviceHistoryChildrenDto.setMaterialValueOrg(mapperUtils.map(materialValueOrgHistoryChildren.getMaterialValueOrg(), MaterialValueOrgDto.class));
                deviceHistoryChildrenDtoList.add(deviceHistoryChildrenDto);
            }

            return deviceHistoryChildrenDtoList;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void saveFromStorageIncome(MaterialValueOrg materialValueOrgParent, List<MaterialValueOrg> MaterialValueOrgChildren, Reason reason, String typeParent, String typeChildren, String newValue, String oldValue, String note) {
        MaterialValueOrgHistory materialValueOrgHistory = new MaterialValueOrgHistory();
        materialValueOrgHistory.setMaterialValueOrg(mapperUtils.map(materialValueOrgParent, MaterialValueOrg.class));
        materialValueOrgHistory.setType(typeParent);
        materialValueOrgHistory.setNewValue(newValue);
        materialValueOrgHistory.setOldValue(oldValue);
        materialValueOrgHistory.setReason(reason);
        materialValueOrgHistory.setNote(note);
        materialValueOrgHistory.setChildren(saveChildrenFromStorageIncome(MaterialValueOrgChildren, materialValueOrgHistory, reason, typeChildren));

        materialValueOrgHistoryRepository.save(materialValueOrgHistory);
    }

    private List<MaterialValueOrgHistory> saveChildrenFromStorageIncome(List<MaterialValueOrg> storageList, MaterialValueOrgHistory parent, Reason reason, String type) {
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = new ArrayList<>();
        if (storageList == null) {
            return null;
        }

        for (MaterialValueOrg storage : storageList) {
            MaterialValueOrgHistory materialValueOrgHistory = new MaterialValueOrgHistory();
            materialValueOrgHistory.setMaterialValueOrg(mapperUtils.map(storage, MaterialValueOrg.class));
            if (type.equals(HistoryTypeEnum.REPLACEMENT.name())) {
                if (materialValueOrgHistoryList.size() == 0) {
                    materialValueOrgHistory.setType(HistoryTypeEnum.REPLACEMENT_TO.name());
                } else {
                    materialValueOrgHistory.setType(HistoryTypeEnum.REPLACEMENT_IN.name());
                }
            } else if (type.equals(HistoryTypeEnum.CARTRIDGE_PRINTER.name())) {
                if (materialValueOrgHistoryList.size() == 0) {
                    materialValueOrgHistory.setType(HistoryTypeEnum.CARTRIDGE_IN.name());
                } else {
                    materialValueOrgHistory.setType(HistoryTypeEnum.CARTRIDGE_OUT.name());
                }
            } else {
                materialValueOrgHistory.setType(type);
            }
            materialValueOrgHistory.setReason(reason);
            if (type.equals(HistoryTypeEnum.INCOME.name())) {
                materialValueOrgHistory.setChildren(saveChildrenFromStorageIncome(storage.getChildren(), materialValueOrgHistory, reason, type));
            }
            materialValueOrgHistory.setParent(parent);

            materialValueOrgHistoryRepository.save(materialValueOrgHistory);

            materialValueOrgHistoryList.add(materialValueOrgHistory);
        }
        return materialValueOrgHistoryList;
    }

    private Optional<Reason> findReasonById(UUID id) {
        if (id != null) {
            Optional<Reason> statementFind = reasonRepository.findById(id);
            if (statementFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Основание с ID " + id + " не найден!");
            }
            return statementFind;
        } else {
            return Optional.empty();
        }
    }

    private Optional<MaterialValueOrgHistory> findMaterialValueHistoryById(UUID id) {
        if (id != null) {
            Optional<MaterialValueOrgHistory> materialValueHistoryFind = materialValueOrgHistoryRepository.findById(id);
            if (materialValueHistoryFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "История с ID " + id + " не найден!");
            }
            return materialValueHistoryFind;
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public List<MaterialValueOrgHistoryDto> saveHistory(List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList) {
        List<MaterialValueOrgHistoryDto> returnMaterialValueOrgHistoryDto = new ArrayList<>();

        for (MaterialValueOrgHistoryDto materialValueOrgHistoryDto : materialValueOrgHistoryDtoList) {
            Reason statement = findReasonById(materialValueOrgHistoryDto.getReasonId()).orElse(null);
            MaterialValueOrgHistory materialValueOrgHistory = findMaterialValueHistoryById(materialValueOrgHistoryDto.getId()).orElse(null);
            materialValueOrgHistory.setReason(statement);
            saveHistoryParent(materialValueOrgHistory.getParent(), statement);
            saveHistoryChildren(materialValueOrgHistory.getChildren(), statement);
            materialValueOrgHistoryRepository.save(materialValueOrgHistory);

            returnMaterialValueOrgHistoryDto.add(mapperUtils.map(materialValueOrgHistory, MaterialValueOrgHistoryDto.class));
        }

        return returnMaterialValueOrgHistoryDto;
    }

    public void saveHistoryParent(MaterialValueOrgHistory materialValueOrgHistoryParent, Reason statement) {
        if (materialValueOrgHistoryParent!=null) {
            materialValueOrgHistoryParent.setReason(statement);
            saveHistoryParent(materialValueOrgHistoryParent.getParent(), statement);
            materialValueOrgHistoryRepository.save(materialValueOrgHistoryParent);
        }
    }

    public void saveHistoryChildren(List<MaterialValueOrgHistory> materialValueOrgHistoryChildrenList, Reason statement) {
        for (MaterialValueOrgHistory materialValueOrgHistory : materialValueOrgHistoryChildrenList) {
            materialValueOrgHistory.setReason(statement);
            saveHistoryChildren(materialValueOrgHistory.getChildren(), statement);
            materialValueOrgHistoryRepository.save(materialValueOrgHistory);
        }
    }

    @Override
    public List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNullOrByReason(UUID id) {
        List<String> typeList = new ArrayList<>();
        typeList.add(HistoryTypeEnum.INCOME.name());
        typeList.add(HistoryTypeEnum.CARTRIDGE_REFILL.name());
        typeList.add(HistoryTypeEnum.DISPOSE_OF.name());
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByTypeInAndParentIdIsNullAndReasonIsNullOrReasonId(typeList, id);
        return mapperUtils.mapAll(materialValueOrgHistoryList, MaterialValueOrgHistoryDto.class);
    }

    @Override
    public List<MaterialValueOrgHistoryDto> findAllForContractByReasonIsNull() {
        List<String> typeList = new ArrayList<>();
        typeList.add(HistoryTypeEnum.INCOME.name());
        typeList.add(HistoryTypeEnum.CARTRIDGE_REFILL.name());
        typeList.add(HistoryTypeEnum.DISPOSE_OF.name());
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByTypeInAndParentIdIsNullAndReasonIsNull(typeList);
        return mapperUtils.mapAll(materialValueOrgHistoryList, MaterialValueOrgHistoryDto.class);
    }

    @Override
    public List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNullOrByReason(UUID id) {
        List<String> typeList = new ArrayList<>();
        typeList.add(HistoryTypeEnum.INCOME.name());
        typeList.add(HistoryTypeEnum.CARTRIDGE_REFILL.name());
        typeList.add(HistoryTypeEnum.DISPOSE_OF.name());
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByTypeNotInAndParentIdIsNullAndReasonIsNullOrReasonId(typeList, id);
        return mapperUtils.mapAll(materialValueOrgHistoryList, MaterialValueOrgHistoryDto.class);
    }

    @Override
    public List<MaterialValueOrgHistoryDto> findAllForStatementByReasonIsNull() {
        List<String> typeList = new ArrayList<>();
        typeList.add(HistoryTypeEnum.INCOME.name());
        typeList.add(HistoryTypeEnum.CARTRIDGE_REFILL.name());
        typeList.add(HistoryTypeEnum.DISPOSE_OF.name());
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByTypeNotInAndParentIdIsNullAndReasonIsNull(typeList);
        return mapperUtils.mapAll(materialValueOrgHistoryList, MaterialValueOrgHistoryDto.class);
    }

    @Override
    public List<MaterialValueOrgHistoryDto> getAllHistoryByReasonId(UUID id) {
        List<MaterialValueOrgHistory> materialValueOrgHistoryList = materialValueOrgHistoryRepository.findAllByReasonIdAndParentIdIsNull(id);
        return mapperUtils.mapAll(materialValueOrgHistoryList, MaterialValueOrgHistoryDto.class);
    }

    @Override
    @Transactional
    public List<MaterialValueOrgHistoryDto> deleteReasonInHistory(List<MaterialValueOrgHistoryDto> materialValueOrgHistoryDtoList) {
        List<MaterialValueOrgHistoryDto> returnMaterialValueOrgHistoryDtoList = new ArrayList<>();
        for (MaterialValueOrgHistoryDto materialValueOrgHistoryDto : materialValueOrgHistoryDtoList) {
            MaterialValueOrgHistory materialValueOrgHistory = findMaterialValueHistoryById(materialValueOrgHistoryDto.getId()).orElse(null);
            materialValueOrgHistory.setReason(null);
            materialValueOrgHistoryRepository.save(materialValueOrgHistory);
            returnMaterialValueOrgHistoryDtoList.add(mapperUtils.map(materialValueOrgHistory, MaterialValueOrgHistoryDto.class));
        }
        return returnMaterialValueOrgHistoryDtoList;
    }

}
