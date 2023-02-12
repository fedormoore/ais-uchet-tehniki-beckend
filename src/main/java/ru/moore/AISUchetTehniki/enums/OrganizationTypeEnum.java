package ru.moore.AISUchetTehniki.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;

@Getter
public enum OrganizationTypeEnum {

    ORGANIZATION("Организация"),
    BRANCH("Филиал"),
    SUBORDINATE("Подведомственная организация"),
    STRUCTURE("Структура (управление, отдел)");

    private String name;

    OrganizationTypeEnum(String name) {
        this.name = name;
    }

    public static String convertToEntityAttribute(String value) {
        for (OrganizationTypeEnum sta : OrganizationTypeEnum.values()) {
            if (sta.name().equals(value)) {
                return sta.getName();
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Unknown database value:" + value);
    }

    public static String convertToDatabaseColumn(String value) {
        for (OrganizationTypeEnum sta : OrganizationTypeEnum.values()) {
            if (sta.getName().equals(value)) {
                return sta.name();
            }
            if (sta.name().equals(value)) {
                return sta.name();
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Unknown database value: " + value);
    }
}
