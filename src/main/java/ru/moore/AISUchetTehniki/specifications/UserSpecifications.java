package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;

import javax.persistence.criteria.Predicate;

public class UserSpecifications {

    public static Specification<User> whereEmail(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> whereFio(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate lastName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + name.toLowerCase() + "%");
            Predicate firstName = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%");
            Predicate middleNames = criteriaBuilder.like(criteriaBuilder.lower(root.get("middleNames")), "%" + name.toLowerCase() + "%");
            return criteriaBuilder.or(lastName, firstName, middleNames);
        };
    }

    public static Specification<User> whereTelephone(String telephone) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("telephone")), "%" + telephone.toLowerCase() + "%");
    }

    public static Specification<User> whereLocation(String location) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("location").get("name")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<User> whereOrganizationFunction(String organizationFunction) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("organizationFunction")), "%" + organizationFunction.toLowerCase() + "%");
    }

    public static Specification<User> whereOrganization(String organization) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("name")), "%" + organization.toLowerCase() + "%");
    }


    public static Specification<User> build(MultiValueMap<String, String> params) {
        Specification<User> spec = Specification.where(null);
        if (params.containsKey("email") && !params.getFirst("email").isBlank()) {
            for (int i = 0; i < params.get("email").size(); i++) {
                spec = spec.or(whereEmail(params.get("email").get(i)));
            }
        }
        if (params.containsKey("fio") && !params.getFirst("fio").isBlank()) {
            for (int i = 0; i < params.get("fio").size(); i++) {
                spec = spec.or(whereFio(params.get("fio").get(i)));
            }
        }
        if (params.containsKey("telephone") && !params.getFirst("telephone").isBlank()) {
            for (int i = 0; i < params.get("telephone").size(); i++) {
                spec = spec.or(whereTelephone(params.get("telephone").get(i)));
            }
        }
        if (params.containsKey("location") && !params.getFirst("location").isBlank()) {
            for (int i = 0; i < params.get("location").size(); i++) {
                spec = spec.or(whereLocation(params.get("location").get(i)));
            }
        }
        if (params.containsKey("organization") && !params.getFirst("organization").isBlank()) {
            for (int i = 0; i < params.get("organization").size(); i++) {
                spec = spec.or(whereOrganization(params.get("organization").get(i)));
            }
        }
        if (params.containsKey("organizationFunction") && !params.getFirst("organizationFunction").isBlank()) {
            for (int i = 0; i < params.get("organizationFunction").size(); i++) {
                spec = spec.or(whereOrganizationFunction(params.get("organizationFunction").get(i)));
            }
        }

        return spec;
    }

}
