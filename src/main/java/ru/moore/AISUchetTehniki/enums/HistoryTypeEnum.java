package ru.moore.AISUchetTehniki.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;

@Getter
public enum HistoryTypeEnum {

    INCOME("Поступление"),
    LOCATION("Изменение кабинета (склада)"),
    BUDGET_ACCOUNT("Изменение бюджетного счета"),
    RESPONSIBLE("Изменение МОЛ"),
    ORGANIZATION("Изменение организацию"),
    ASSEMBLE("Сформировали новую МЦ"),
    ASSEMBLE_IN("Ввели в состав в следствии сформирования новой МЦ"),
    DISASSEMBLE("Расформировали МЦ"),
    DISASSEMBLE_OUT("Вывели из состава в следствии расформирования МЦ"),
    ADD_DEVICE("Включили в состав новую МЦ"),
    ADD_DEVICE_IN("Включили в состав"),
    REMOVE_DEVICE("Вывели из состава"),
    REMOVE_DEVICE_OUT("Вывели из МЦ"),
    REPLACEMENT("Заменили оборудование"),
    REPLACEMENT_IN("Включили в оборудование в связи с заменой"),
    REPLACEMENT_TO("Вывели из оборудования в связи с заменой"),
    REPAIR("Ремонт"),
    STORAGE_TO_REGISTRY("Выдали"),
    REGISTRY_TO_STORAGE("Вернули на склад"),
    USER("Закрепили за сотрудником"),
    INV_NUMBER("Присвоили инвентарный номер"),
    CARTRIDGE_OUT("Убрали картридж"),
    CARTRIDGE_IN("Установили картридж"),
    CARTRIDGE_PRINTER("Заменили картридж"),
    CARTRIDGE_REFILL("Заправили картридж"),
    WRITE_OFF("Списали"),
    DISPOSE_OF("Утилизировали");

    private String name;

    HistoryTypeEnum(String name) {
        this.name = name;
    }

    public static String convertToEntityAttribute(String value) {
        for (HistoryTypeEnum sta : HistoryTypeEnum.values()) {
            if (sta.name().equals(value)) {
                return sta.getName();
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Unknown database value:" + value);
    }

    public static String convertToDatabaseColumn(String value) {
        for (HistoryTypeEnum sta : HistoryTypeEnum.values()) {
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
