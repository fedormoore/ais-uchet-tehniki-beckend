package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.spr.BudgetAccount;

public class BudgetAccountSpecifications {

    public static Specification<BudgetAccount> whereName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<BudgetAccount> whereCode(String code) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<BudgetAccount> build(MultiValueMap<String, String> params) {
        Specification<BudgetAccount> spec = Specification.where(null);
        if (params.containsKey("name") && !params.getFirst("name").isBlank()) {
            for (int i = 0; i < params.get("name").size(); i++) {
                spec = spec.or(whereName(params.get("name").get(i)));
            }
        }
        if (params.containsKey("code") && !params.getFirst("code").isBlank()) {
            for (int i = 0; i < params.get("code").size(); i++) {
                spec = spec.or(whereCode(params.get("code").get(i)));
            }
        }
        return spec;
    }

}
