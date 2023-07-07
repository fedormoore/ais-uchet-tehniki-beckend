package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.enums.OrganizationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.IncomeDto;
import ru.moore.AISUchetTehniki.models.Entity.Reason;
import ru.moore.AISUchetTehniki.models.Entity.spr.*;
import ru.moore.AISUchetTehniki.services.ImportFromExcelMaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.ReasonService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.IncomeService;
import ru.moore.AISUchetTehniki.services.spr.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportFromExcelMaterialValueOrgServiceImpl implements ImportFromExcelMaterialValueOrgService {

    private final IncomeService incomeService;
    private final OrganizationService organizationService;
    private final ReasonService reasonService;
    private final UserService userService;
    private final LocationService locationService;
    private final MaterialValueTypeService materialValueTypeService;
    private final MaterialValueService materialValueService;
    private final BudgetAccountService budgetAccountService;

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Import";

    @Override
    public ResponseEntity<String> importFromExcel(MultipartFile file, Authentication authentication) {
        //проверка: файл действительно excel
        checkFileIsExcel(file);
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            //проверка: есть ли лист с наименованием Import. Если есть то, присваиваем значение
            Sheet sheet = getSheet(workbook);
            //проверка: соответствует ли шаблон
            checkIsTemplate(sheet);
            //проверка: заполнение обязательных полей для заголовка
            checkIsHeader(sheet);

            String organizationValue = getCellValue(sheet, "A3");
            Optional<Organization> organization = organizationService.findByName(organizationValue);
            if (organization.isEmpty()) {
                organization = Optional.of(new Organization());
                organization.get().setName(organizationValue);
                organization.get().setType(OrganizationTypeEnum.ORGANIZATION.getName());
                organizationService.saveOrganization(organization.get());
            }

            String reasonDateValue = getCellValue(sheet, "B3");
            String reasonNumberValue = getCellValue(sheet, "C3");

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

            Optional<Reason> reason = reasonService.findByTypeRecordAndDateAndNumber("contract", formatter.parse(reasonDateValue), reasonNumberValue);
            if (reason.isEmpty()) {
                reason = Optional.of(new Reason());
                reason.get().setTypeRecord("contract");
                reason.get().setDate(formatter.parse(reasonDateValue));
                reason.get().setNumber(reasonNumberValue);
                reasonService.saveReason(reason.get());
            }

            String locationValue = getCellValue(sheet, "D3");
            Optional<Location> location = locationService.findByName(locationValue);
            if (location.isEmpty()) {
                location = Optional.of(new Location());
                location.get().setName(locationValue);
                location.get().setType(LocationTypeEnum.STORAGE.getName());
                locationService.saveLocation(location.get());
            }

            String lastNameValue = getCellValue(sheet, "E3");
            String firstNameValue = getCellValue(sheet, "F3");
            String middleNameValue = getCellValue(sheet, "G3");
            if (!lastNameValue.equals("") && firstNameValue.equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Если заполнена ячейка 'Фамилия МОЛ', то и 'Имя МОЛ' тоже должно быть заполнено");
            }
            Optional<User> user = userService.findByLastNameAndFirstNameAndMiddleNames(lastNameValue, firstNameValue, middleNameValue);
            if (user.isEmpty()) {
                user = Optional.of(new User());
                user.get().setEmail(lastNameValue + "@" + firstNameValue + "." + middleNameValue);
                user.get().setLastName(lastNameValue);
                user.get().setFirstName(firstNameValue);
                user.get().setMiddleNames(middleNameValue);
                userService.saveUser(user.get());
            }

            IncomeDto incomeDto = new IncomeDto();
            incomeDto.setOrganizationId(organization.get().getId());
            incomeDto.setContractId(reason.get().getId());
            incomeDto.setLocationId(location.get().getId());
            incomeDto.setResponsibleId(user.get().getId());

            //проверка: есть ли данные для импорта. Если есть то, присваиваем значение
            Iterator<Row> rows = getRows(sheet);
            int rowNumber = 1;

            List<IncomeDto.IncomeSpecDto> incomeSpecDtoList = new ArrayList<>();

            do {
                rows.next();
                //пропускаем первые 5 строк
                if (rowNumber <= 5) {
                    rowNumber++;
                    continue;
                }

                IncomeDto.IncomeSpecDto incomeSpecDto = new IncomeDto.IncomeSpecDto();

                String materialValueTypeValue = getCellValue(sheet, "A" + rowNumber);
                checkIsNullCell(materialValueTypeValue, "Тип");
                Optional<MaterialValueType> materialValueType = materialValueTypeService.findByName(materialValueTypeValue);
                if (materialValueType.isEmpty()) {
                    materialValueType = Optional.of(new MaterialValueType());
                    materialValueType.get().setName(materialValueTypeValue);
                    materialValueTypeService.saveMaterialValueType(materialValueType.get());
                }

                String materialValueNameInOrgValue = getCellValue(sheet, "B" + rowNumber);
                String materialValueNameFirmValue = getCellValue(sheet, "C" + rowNumber);
                String materialValueNameModelValue = getCellValue(sheet, "D" + rowNumber);
                if (!materialValueNameFirmValue.equals("") && materialValueNameModelValue.equals("")) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Если заполнена ячейка 'Фирма', то и 'Модель' тоже должно быть заполнено");
                }
                Optional<MaterialValue> materialValue = materialValueService.findByNameInOrgAndNameFirmAndNameModel(materialValueNameInOrgValue, materialValueNameFirmValue, materialValueNameModelValue);
                if (materialValue.isEmpty()) {
                    materialValue = Optional.of(new MaterialValue());
                    materialValue.get().setMaterialValueType(materialValueType.get());
                    materialValue.get().setNameInOrg(materialValueNameInOrgValue);
                    materialValue.get().setNameFirm(materialValueNameFirmValue);
                    materialValue.get().setNameModel(materialValueNameModelValue);
                    materialValueService.saveMaterialValue(materialValue.get());
                }

                String sumValue = getCellValue(sheet, "E" + rowNumber);
                checkIsNullCell(sumValue, "Сумма");

                String budgetAccountCodeValue = getCellValue(sheet, "F" + rowNumber);
                String budgetAccountNameValue = getCellValue(sheet, "G" + rowNumber);
                checkIsNullCell(budgetAccountCodeValue, "Код");
                checkIsNullCell(budgetAccountNameValue, "Наименование");
                Optional<BudgetAccount> budgetAccount = budgetAccountService.findByCodeAndName(budgetAccountCodeValue, budgetAccountNameValue);
                if (budgetAccount.isEmpty()) {
                    budgetAccount = Optional.of(new BudgetAccount());
                    budgetAccount.get().setCode(budgetAccountCodeValue);
                    budgetAccount.get().setName(budgetAccountNameValue);
                    budgetAccountService.saveBudgetAccount(budgetAccount.get());
                }

                incomeSpecDto.setMaterialValueId(materialValue.get().getId());
                incomeSpecDto.setSum(Double.valueOf(sumValue));
                incomeSpecDto.setBudgetAccountId(budgetAccount.get().getId());
                incomeSpecDtoList.add(incomeSpecDto);
                rowNumber++;
            } while (rows.hasNext());
            incomeDto.setSpec(incomeSpecDtoList);
            incomeService.saveIncome(incomeDto, authentication);
            workbook.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }

        return new ResponseEntity<>("Успешный импорт", HttpStatus.OK);
    }

    private void checkFileIsExcel(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Файл не является Excel");
        }
    }

    private Sheet getSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheet(SHEET);
        if (sheet == null) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Не найден лист Import");
        }
        return sheet;
    }

    private void checkIsTemplate(Sheet sheet) {
        String[] cellNames = new String[]{"A", "B", "C", "D", "E", "F", "G"};
        //проверка 1 строки
        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "1");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("Организация")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Организация'. Ячейка A1");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("Основание")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Основание'. Ячейка B1");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка B1 и C1 должны быть объединены");
            }
            if (cellName.equals("D") && !cell.getStringCellValue().equals("Склад")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Склад'. Ячейка D1");
            }
            if (cellName.equals("E") && !cell.getStringCellValue().equals("МОЛ")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'МОЛ'. Ячейка D1");
            }
            if (cellName.equals("F") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка E1 и F1 должны быть объединены");
            }
            if (cellName.equals("G") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка F1 и G1 должны быть объединены");
            }
        }

        //проверка 2 строки
        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "2");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка A1 и A2 должны быть объединены");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("Дата")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Дата'. Ячейка B2");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("Номер")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Номер'. Ячейка С2");
            }
            if (cellName.equals("D") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка D1 и D2 должны быть объединены");
            }
            if (cellName.equals("E") && !cell.getStringCellValue().equals("Фамилия")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Фамилия'. Ячейка E2");
            }
            if (cellName.equals("F") && !cell.getStringCellValue().equals("Имя")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Имя'. Ячейка F2");
            }
            if (cellName.equals("G") && !cell.getStringCellValue().equals("Отчество")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поля 'Отчество'. Ячейка G2");
            }
        }

        //проверка 4 строки
        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "4");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("Наименование материальной ценности")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Наименование материальной ценности'. Ячейка A4");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка A4 и B4 должны быть объединены");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка C4 и D4 должны быть объединены");
            }
            if (cellName.equals("D") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка D4 и C4 должны быть объединены");
            }
            if (cellName.equals("E") && !cell.getStringCellValue().equals("Стоимость")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Стоимость'. Ячейка E4");
            }
            if (cellName.equals("F") && !cell.getStringCellValue().equals("Бюджетный счет")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Бюджетный счет'. Ячейка F4");
            }
            if (cellName.equals("G") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка F4 и G4 должны быть объединены");
            }
        }

        //проверка 5 строки
        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "5");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("Тип")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Тип'. Ячейка A5");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("Наименование в организации")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Наименование в организации'. Ячейка B5");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("Фирма")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Фирма'. Ячейка C5");
            }
            if (cellName.equals("D") && !cell.getStringCellValue().equals("Модель")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Модель'. Ячейка D5");
            }
            if (cellName.equals("E") && !cell.getStringCellValue().equals("")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Ячейка E4 и E5 должны быть объединены");
            }
            if (cellName.equals("F") && !cell.getStringCellValue().equals("Код")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Код'. Ячейка F4");
            }
            if (cellName.equals("G") && !cell.getStringCellValue().equals("Наименование")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Наименование'. Ячейка G5");
            }
        }
    }

    private void checkIsHeader(Sheet sheet) {
        CellReference cellReference = new CellReference("D3");
        Row row = sheet.getRow(cellReference.getRow());
        if (row == null) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Нет значения в поле 'Склад'. Ячейка D3");
        }
    }

    private Iterator<Row> getRows(Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();
        int rowSize = 0;
        while (rows.hasNext()) {
            rowSize++;
            rows.next();
        }
        if (rowSize <= 5) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Нет строк для импорта");
        }
        return sheet.iterator();
    }

    private String getCellValue(Sheet sheet, String indexCell) {
        String value = "";
        CellReference cellReference = new CellReference(indexCell);
        Row row = sheet.getRow(cellReference.getRow());
        if (row != null) {
            Cell cell = row.getCell(cellReference.getCol());
            if (cell != null) {
                if (!cell.getCellType().toString().equals("STRING")) {
                    throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Формат ячейки должен быть 'Текстовой'. Ячейка " + indexCell);
                } else {
                    value = cell.getStringCellValue();
                }
            }
        }
        return value;
    }

    private void checkIsNullCell(String value, String nameCell) {
        if (value.equals("")) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Ячейка '" + nameCell + "' должна быть заполнено");
        }
    }
}
