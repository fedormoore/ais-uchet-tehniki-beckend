package ru.moore.AISUchetTehniki.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;

@Getter
public enum LocationTypeEnum {

    COUNTRY("Страна"),
    SUBJECT("Субъект"),
    CITY("Город"),
    ADDRESS("Адрес"),
    FLOOR("Этаж"),
    CABINET("Кабинет"),
    STORAGE("Склад");

    private String name;

    LocationTypeEnum(String name) {
        this.name = name;
    }

    public static String convertToEntityAttribute(String value) {
        for (LocationTypeEnum sta : LocationTypeEnum.values()) {
            if (sta.name().equals(value)) {
                return sta.getName();
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Unknown database value:" + value);
    }

    public static String convertToDatabaseColumn(String value) {
        for (LocationTypeEnum sta : LocationTypeEnum.values()) {
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
