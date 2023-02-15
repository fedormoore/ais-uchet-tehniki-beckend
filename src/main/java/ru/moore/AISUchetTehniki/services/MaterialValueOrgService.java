package ru.moore.AISUchetTehniki.services;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgNotChildrenDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialValueOrgService {
    Page<MaterialValueOrgDto> getAllMaterialValueOrgByLocation(Specification<MaterialValueOrg> spec, int page, int pageSize);

    List<MaterialValueOrgDto> saveMaterialValueOrg(List<MaterialValueOrgDto> materialValueOrgDtoDtoList);

    List<MaterialValueOrgNotChildrenDto> getAllMaterialValueOrgNotExpandable();

    List<MaterialValueOrgDto> getAllMaterialValueOrgParentIdIsNullExpandable();

    List<MaterialValueOrgDto> getAllMaterialValueOrgChildrenIdIsNull();

    List<MaterialValueOrgDto> getAllMaterialValueOrgNameInOrgIsNotNull();

    List<MaterialValueOrgDto> getAllMaterialValueOrgByAddToOtherTrue();

    List<MaterialValueOrgDto> getAllMaterialValueOrgByAddOtherTrue();

    MaterialValueOrgDto toDtoRegistry(MaterialValueOrg registry);

    Optional<MaterialValueOrg> findById(UUID id);

    MaterialValueOrg findByBarcode(String barcode);

    String getName(MaterialValueOrg materialValueOrg);

    List<MaterialValueOrgDto> getAllCartridgeNeedRefiling();

    List<MaterialValueOrgDto> getAllPrinter();

    List<MaterialValueOrgDto> getAllCartridgeFull();

    List<MaterialValueOrgDto> getAllDisposeOf();
}
