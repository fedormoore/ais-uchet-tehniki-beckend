package ru.moore.AISUchetTehniki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialValueOrgRepository extends JpaRepository<MaterialValueOrg, UUID>, JpaSpecificationExecutor<MaterialValueOrg> {

    Optional<MaterialValueOrg> findByBarcode(String barcode);

    List<MaterialValueOrg> findAllByParentIdIsNullAndStatusIsNull();

    List<MaterialValueOrg> findByLocationId(UUID id);

    List<MaterialValueOrg> findByOrganizationId(UUID id);

    List<MaterialValueOrg> findAllByMaterialValueNameInOrgIsNotNullAndStatusIsNull();

    List<MaterialValueOrg> findAllByLocationTypeAndMaterialValueMaterialValueTypeName(String locationType, String deviceType);

    List<MaterialValueOrg> findAllByMaterialValueMaterialValueTypeNameOrderByStatusAsc(String deviceType);

    List<MaterialValueOrg> findAllByMaterialValueMaterialValueTypeNameAndStatusInOrderByStatusAsc(String deviceType, List<String> status);

    List<MaterialValueOrg> findAllByMaterialValueMaterialValueTypeNameAndStatusIn(String deviceType, List<String> status);

    List<MaterialValueOrg> findAllByMaterialValueMaterialValueTypeAddToOtherAndParentIdIsNull(boolean addToOther);

    List<MaterialValueOrg> findAllByMaterialValueMaterialValueTypeAddOtherAndStatusIsNullAndParentIdIsNull(boolean addOther);

    List<MaterialValueOrg> findDistinctByChildrenIsNotNullAndParentIsNullAndStatusIsNull();

    List<MaterialValueOrg> findAllByStatus(String status);

    List<MaterialValueOrg> findByUserId(UUID id);

    List<MaterialValueOrg> findByResponsibleId(UUID id);

    List<MaterialValueOrg> findByMaterialValueId(UUID id);

    List<MaterialValueOrg> findByBudgetAccountId(UUID id);

}
