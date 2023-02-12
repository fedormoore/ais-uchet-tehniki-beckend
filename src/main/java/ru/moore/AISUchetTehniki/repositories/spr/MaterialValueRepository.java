package ru.moore.AISUchetTehniki.repositories.spr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;

import java.util.List;
import java.util.UUID;

public interface MaterialValueRepository extends JpaRepository<MaterialValue, UUID>, JpaSpecificationExecutor<MaterialValue> {

    @Query(value = "select name_firm from spr_material_value where name_firm is not null group by name_firm", nativeQuery = true)
    List<String> findAllFirm();

    List<MaterialValue> findAllByNameInOrgIsNotNull();

    List<MaterialValue> findAllByMaterialValueTypeAddToOther(boolean b);

    List<MaterialValue> findByMaterialValueTypeId(UUID id);
}
