package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Entity.Reason;

import javax.persistence.criteria.Predicate;

public class ContractSpecifications {

    public static Specification<Reason> whereTypeRecordContract() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate locationStorage = criteriaBuilder.equal(root.get("typeRecord"), "contract");
            return criteriaBuilder.and(locationStorage);
        };
    }

    public static Specification<Reason> whereTypeRecordStatement() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate locationCabinet = criteriaBuilder.equal(root.get("typeRecord"), "statement");
            return criteriaBuilder.and(locationCabinet);
        };
    }

    public static Specification<Reason> whereDate(String date) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("date")), "%" + date.toLowerCase() + "%");
    }

    public static Specification<Reason> whereNumber(String number) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("number")), "%" + number.toLowerCase() + "%");
    }

    public static Specification<Reason> whereSum(String sum) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("sum")), "%" + sum.toLowerCase() + "%");
    }

    public static Specification<Reason> whereCounterparty(String counterparty) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("counterparty")), "%" + counterparty.toLowerCase() + "%");
    }

    public static Specification<Reason> whereOrganization(String organization) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("name")), "%" + organization.toLowerCase() + "%");
    }

    public static Specification<Reason> build(MultiValueMap<String, String> params) {
        Specification<Reason> spec = Specification.where(null);
        if (params.containsKey("typeRecord")) {
            if (params.getFirst("typeRecord").equals("contract")) {
                spec = spec.and(whereTypeRecordContract());
            }
            if (params.getFirst("typeRecord").equals("statement")) {
                spec = spec.and(whereTypeRecordStatement());
            }
        }
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
        if (params.containsKey("sum") && !params.getFirst("sum").isBlank()) {
            for (int i = 0; i < params.get("sum").size(); i++) {
                spec = spec.or(whereSum(params.get("sum").get(i)));
            }
        }
        if (params.containsKey("counterparty") && !params.getFirst("counterparty").isBlank()) {
            for (int i = 0; i < params.get("counterparty").size(); i++) {
                spec = spec.or(whereCounterparty(params.get("counterparty").get(i)));
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
