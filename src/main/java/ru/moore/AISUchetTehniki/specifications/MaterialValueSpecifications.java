package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValue;

public class MaterialValueSpecifications {

    public static Specification<MaterialValue> whereType(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("materialValueType").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValue> whereNameInOrg(String nameInOrg) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nameInOrg")), "%" + nameInOrg.toLowerCase() + "%");
    }

    public static Specification<MaterialValue> whereNameFirm(String nameFirm) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nameFirm")), "%" + nameFirm.toLowerCase() + "%");
    }

    public static Specification<MaterialValue> whereNameModel(String nameModel) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nameModel")), "%" + nameModel.toLowerCase() + "%");
    }

    public static Specification<MaterialValue> build(MultiValueMap<String, String> params) {
        Specification<MaterialValue> spec = Specification.where(null);
        if (params.containsKey("materialValueType") && !params.getFirst("materialValueType").isBlank()) {
            for (int i = 0; i < params.get("materialValueType").size(); i++) {
                spec = spec.or(whereType(params.get("materialValueType").get(i)));
            }
        }
        if (params.containsKey("nameInOrg") && !params.getFirst("nameInOrg").isBlank()) {
            for (int i = 0; i < params.get("nameInOrg").size(); i++) {
                spec = spec.or(whereNameInOrg(params.get("nameInOrg").get(i)));
            }
        }
        if (params.containsKey("nameFirm") && !params.getFirst("nameFirm").isBlank()) {
            for (int i = 0; i < params.get("nameFirm").size(); i++) {
                spec = spec.or(whereNameFirm(params.get("nameFirm").get(i)));
            }
        }
        if (params.containsKey("nameModel") && !params.getFirst("nameModel").isBlank()) {
            for (int i = 0; i < params.get("nameModel").size(); i++) {
                spec = spec.or(whereNameModel(params.get("nameModel").get(i)));
            }
        }
        return spec;
    }

}
