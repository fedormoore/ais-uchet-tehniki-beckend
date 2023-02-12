package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.spr.MaterialValueType;

public class MaterialValueTypeSpecifications {

    public static Specification<MaterialValueType> whereName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueType> build(MultiValueMap<String, String> params) {
        Specification<MaterialValueType> spec = Specification.where(null);
        if (params.containsKey("name") && !params.getFirst("name").isBlank()) {
            for (int i = 0; i < params.get("name").size(); i++) {
                spec = spec.or(whereName(params.get("name").get(i)));
            }
        }
        return spec;
    }

}
