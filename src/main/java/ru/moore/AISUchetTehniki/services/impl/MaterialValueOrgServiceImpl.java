package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgNotChildrenDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.spr.BudgetAccountService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.services.spr.UserService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialValueOrgServiceImpl implements MaterialValueOrgService {

    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final LocationService locationService;
    private final BudgetAccountService budgetAccountService;
    private final UserService userService;
    private final MapperUtils mapperUtils;

    @Override
    public Page<MaterialValueOrgDto> getAllMaterialValueOrgByLocation(Specification<MaterialValueOrg> spec, int page, int pageSize) {
        return materialValueOrgRepository.findAll(spec, PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "createdAt"))).map(registry -> toDtoRegistry(registry));
    }

    @Override
    @Transactional
    public List<MaterialValueOrgDto> saveMaterialValueOrg(List<MaterialValueOrgDto> materialValueOrgDtoDtoList) {
        List<MaterialValueOrgDto> returnMaterialValueOrgDtoDtoList = new ArrayList<>();
        for (MaterialValueOrgDto materialValueOrgDto : materialValueOrgDtoDtoList) {
            MaterialValueOrg materialValueOrg = findById(materialValueOrgDto.getId()).orElse(null);
            Location location = locationService.findById(materialValueOrgDto.getLocationId()).orElse(null);
            BudgetAccount budgetAccount = budgetAccountService.findById(materialValueOrgDto.getBudgetAccountId()).orElse(null);
            User responsible = userService.findById(materialValueOrgDto.getResponsibleId()).orElse(null);
            User user = userService.findById(materialValueOrgDto.getUserId()).orElse(null);

            materialValueOrg.setBarcode(materialValueOrgDto.getBarcode());
            materialValueOrg.setLocation(location);
            materialValueOrg.setBudgetAccount(budgetAccount);
            materialValueOrg.setResponsible(responsible);
            materialValueOrg.setUser(user);
            materialValueOrg.setInvNumber(materialValueOrgDto.getInvNumber());

            materialValueOrgRepository.save(materialValueOrg);
            returnMaterialValueOrgDtoDtoList.add(materialValueOrgDto);
        }
        return returnMaterialValueOrgDtoDtoList;
    }

    @Override
    public List<MaterialValueOrgNotChildrenDto> getAllMaterialValueOrgNotExpandable() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAll(), MaterialValueOrgNotChildrenDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllMaterialValueOrgParentIdIsNullExpandable() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByParentIdIsNullAndStatusIsNull(), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllMaterialValueOrgChildrenIdIsNull() {
        return mapperUtils.mapAll(materialValueOrgRepository.findDistinctByChildrenIsNotNullAndParentIsNullAndStatusIsNull(), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllMaterialValueOrgNameInOrgIsNotNull() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByMaterialValueNameInOrgIsNotNullAndStatusIsNull(), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllMaterialValueOrgByAddToOtherTrue() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeAddToOtherAndParentIdIsNull(true), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllMaterialValueOrgByAddOtherTrue() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeAddOtherAndStatusIsNullAndParentIdIsNull(true), MaterialValueOrgDto.class);
    }

    @Override
    public MaterialValueOrgDto toDtoRegistry(MaterialValueOrg registry) {
        return mapperUtils.map(registry, MaterialValueOrgDto.class);
    }

    @Override
    public Optional<MaterialValueOrg> findById(UUID id) {
        if (id != null) {
            Optional<MaterialValueOrg> materialValueOrgFind = materialValueOrgRepository.findById(id);
            if (materialValueOrgFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Материальная ценность с ID " + id + " не найдено!");
            }
            return materialValueOrgFind;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public MaterialValueOrg findByBarcode(String barcode) {
        if (barcode != null) {
            Optional<MaterialValueOrg> materialValueOrgFind = materialValueOrgRepository.findByBarcode(barcode);
            if (materialValueOrgFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Материальная ценность с barcode " + barcode + " не найдена!");
            }
            return materialValueOrgFind.get();
        } else {
            return null;
        }
    }

    @Override
    public String getName(MaterialValueOrg materialValueOrg) {
        if (materialValueOrg != null) {
            if (materialValueOrg.getMaterialValue().getNameInOrg() != null && materialValueOrg.getMaterialValue().getNameModel() != null) {
                return materialValueOrg.getMaterialValue().getMaterialValueType().getName() + " " + materialValueOrg.getMaterialValue().getNameInOrg() + "(" + materialValueOrg.getMaterialValue().getNameFirm() + " " + materialValueOrg.getMaterialValue().getNameModel() + ")";
            }
            if (materialValueOrg.getMaterialValue().getNameInOrg() != null) {
                return materialValueOrg.getMaterialValue().getMaterialValueType().getName() + " " + materialValueOrg.getMaterialValue().getNameInOrg();
            }
            return materialValueOrg.getMaterialValue().getMaterialValueType().getName() + " " + materialValueOrg.getMaterialValue().getNameFirm() + " " + materialValueOrg.getMaterialValue().getNameModel();
        } else {
            return "";
        }
    }

    @Override
    public List<MaterialValueOrgDto> getAllCartridgeNeedRefiling() {
        List<String> stringList = new ArrayList<>();
        stringList.add(RegistryStatusEnum.CARTRIDGE_NEEDS_REFILLING.name());
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeNameAndStatusIn("Картридж", stringList), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllPrinter() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByLocationTypeAndMaterialValueMaterialValueTypeName(LocationTypeEnum.CABINET.name(), "Принтер"), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllCartridgeFull() {
        List<String> stringList = new ArrayList<>();
        stringList.add(RegistryStatusEnum.CARTRIDGE_REFILL.name());
        stringList.add(RegistryStatusEnum.CARTRIDGE_NEW.name());
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByMaterialValueMaterialValueTypeNameAndStatusIn("Картридж", stringList), MaterialValueOrgDto.class);
    }

    @Override
    public List<MaterialValueOrgDto> getAllDisposeOf() {
        return mapperUtils.mapAll(materialValueOrgRepository.findAllByStatus(RegistryStatusEnum.WRITE_OFF.name()), MaterialValueOrgDto.class);
    }
}
