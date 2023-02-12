package ru.moore.AISUchetTehniki.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;

@Getter
public enum RegistryStatusEnum {

    CARTRIDGE_NEW("Новый"),
    CARTRIDGE_NEEDS_REFILLING("Пустой"),
    CARTRIDGE_REFILL("Запрпавлен"),
    CARTRIDGE_IN_PRINTER("Установлен в принтер"),
    WRITE_OFF("Списан"),
    DISPOSE_OF("Утилизирован"),

    DISASSEMBLE("Раскомплектован");

    private String name;

    RegistryStatusEnum(String name) {
        this.name = name;
    }

    public static String convertToEntityAttribute(String value) {
        for (RegistryStatusEnum sta : RegistryStatusEnum.values()) {
            if (sta.name().equals(value)) {
                return sta.getName();
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Unknown database value:" + value);
    }

    public static String convertToDatabaseColumn(String value) {
        for (RegistryStatusEnum sta : RegistryStatusEnum.values()) {
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
