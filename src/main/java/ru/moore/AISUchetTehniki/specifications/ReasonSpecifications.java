package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.Reason;

public class ReasonSpecifications {

    public static Specification<Reason> whereDate(String date) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("date")), "%" + date.toLowerCase() + "%");
    }

    public static Specification<Reason> whereNumber(String number) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("number")), "%" + number.toLowerCase() + "%");
    }

    public static Specification<Reason> whereOrganization(String organization) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("name")), "%" + organization.toLowerCase() + "%");
    }

    public static Specification<Reason> build(MultiValueMap<String, String> params) {
        Specification<Reason> spec = Specification.where(null);
        if (params.containsKey("date") && !params.getFirst("date").isBlank()) {
            for (int i = 0; i < params.get("date").size(); i++) {
                spec = spec.or(whereDate(params.get("date").get(i)));
            }
        }
        if (params.containsKey("number") && !params.getFirst("number").isBlank()) {
            for (int i = 0; i < params.get("number").size(); i++) {
                spec = spec.or(whereNumber(params.get("number").get(i)));
            }
        }
        if (params.containsKey("organization") && !params.getFirst("organization").isBlank()) {
            for (int i = 0; i < params.get("organization").size(); i++) {
                spec = spec.or(whereOrganization(params.get("organization").get(i)));
            }
        }
        return spec;
    }

}
