package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.spr.Counterparty;

public class CounterpartySpecifications {

    public static Specification<Counterparty> whereName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Counterparty> whereInn(String inn) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("inn")), "%" + inn.toLowerCase() + "%");
    }

    public static Specification<Counterparty> whereTelephone(String telephone) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("telephone")), "%" + telephone.toLowerCase() + "%");
    }

    public static Specification<Counterparty> whereEmail(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Counterparty> whereContact(String contact) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("contact")), "%" + contact.toLowerCase() + "%");
    }

    public static Specification<Counterparty> build(MultiValueMap<String, String> params) {
        Specification<Counterparty> spec = Specification.where(null);
        if (params.containsKey("name") && !params.getFirst("name").isBlank()) {
            for (int i = 0; i < params.get("name").size(); i++) {
                spec = spec.or(whereName(params.get("name").get(i)));
            }
        }
        if (params.containsKey("inn") && !params.getFirst("inn").isBlank()) {
            for (int i = 0; i < params.get("inn").size(); i++) {
                spec = spec.or(whereInn(params.get("inn").get(i)));
            }
        }
        if (params.containsKey("telephone") && !params.getFirst("telephone").isBlank()) {
            for (int i = 0; i < params.get("telephone").size(); i++) {
                spec = spec.or(whereTelephone(params.get("telephone").get(i)));
            }
        }
        if (params.containsKey("email") && !params.getFirst("email").isBlank()) {
            for (int i = 0; i < params.get("email").size(); i++) {
                spec = spec.or(whereEmail(params.get("email").get(i)));
            }
        }
        if (params.containsKey("contact") && !params.getFirst("contact").isBlank()) {
            for (int i = 0; i < params.get("contact").size(); i++) {
                spec = spec.or(whereContact(params.get("contact").get(i)));
            }
        }
        return spec;
    }

}
