package ru.moore.AISUchetTehniki.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;

import javax.persistence.criteria.Predicate;

public class MaterialValueOrgSpecifications {

    public static Specification<MaterialValueOrg> whereLocationStorage() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate parentIsNull = criteriaBuilder.isNull(root.get("parent"));
            Predicate locationStorage = criteriaBuilder.equal(root.get("location").get("type"), LocationTypeEnum.STORAGE.name());
            return criteriaBuilder.and(parentIsNull, locationStorage);
        };
    }

    public static Specification<MaterialValueOrg> whereLocationCabinet() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate parentIsNull = criteriaBuilder.isNull(root.get("parent"));
            Predicate locationCabinet = criteriaBuilder.equal(root.get("location").get("type"), LocationTypeEnum.CABINET.name());
            return criteriaBuilder.and(parentIsNull, locationCabinet);
        };
    }

    public static Specification<MaterialValueOrg> whereMaterialValue(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Predicate type = criteriaBuilder.like(criteriaBuilder.lower(root.get("materialValue").get("materialValueType").get("name")), "%" + name.toLowerCase() + "%");
            Predicate nameInOrg = criteriaBuilder.like(criteriaBuilder.lower(root.get("materialValue").get("nameInOrg")), "%" + name.toLowerCase() + "%");
            Predicate firm = criteriaBuilder.like(criteriaBuilder.lower(root.get("materialValue").get("nameFirm")), "%" + name.toLowerCase() + "%");
            Predicate model = criteriaBuilder.like(criteriaBuilder.lower(root.get("materialValue").get("nameModel")), "%" + name.toLowerCase() + "%");
            return criteriaBuilder.or(type, nameInOrg, firm, model);
        };
    }

    public static Specification<MaterialValueOrg> whereBarcode(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("barcode")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereStatus(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereOrganization(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("organization").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereLocation(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("location").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereBudgetAccount(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("budgetAccount").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereResponsible(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("responsible").get("lastName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> whereInvNumber(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("invNumber")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<MaterialValueOrg> build(MultiValueMap<String, String> params) {
        Specification<MaterialValueOrg> spec = Specification.where(null);
        if (params.containsKey("locationType")) {
            if (params.getFirst("locationType").equals("storage")) {
                spec = spec.and(whereLocationStorage());
            }
            if (params.getFirst("locationType").equals("cabinet")) {
                spec = spec.and(whereLocationCabinet());
            }
        }
        if (params.containsKey("materialValue") && !params.getFirst("materialValue").isBlank()) {
            for (int i = 0; i < params.get("materialValue").size(); i++) {
                spec = spec.and(whereMaterialValue(params.get("materialValue").get(i)));
            }
        }
        if (params.containsKey("barcode") && !params.getFirst("barcode").isBlank()) {
            for (int i = 0; i < params.get("barcode").size(); i++) {
                spec = spec.and(whereBarcode(params.get("barcode").get(i)));
            }
        }
        if (params.containsKey("status") && !params.getFirst("status").isBlank()) {
            spec = spec.and(whereStatus(params.getFirst("status")));
        }
        if (params.containsKey("organization") && !params.getFirst("organization").isBlank()) {
            spec = spec.and(whereOrganization(params.getFirst("organization")));
        }
        if (params.containsKey("location") && !params.getFirst("location").isBlank()) {
            spec = spec.and(whereLocation(params.getFirst("location")));
        }
        if (params.containsKey("budgetAccount") && !params.getFirst("budgetAccount").isBlank()) {
            spec = spec.and(whereBudgetAccount(params.getFirst("budgetAccount")));
        }
        if (params.containsKey("responsible") && !params.getFirst("responsible").isBlank()) {
            spec = spec.and(whereResponsible(params.getFirst("responsible")));
        }
        if (params.containsKey("invNumber") && !params.getFirst("invNumber").isBlank()) {
            spec = spec.and(whereInvNumber(params.getFirst("invNumber")));
        }
        return spec;
    }

}
