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
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.IncomeDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.services.ImportFromExcelMaterialValueOrgService;
import ru.moore.AISUchetTehniki.services.materialValueOrgAction.IncomeService;
import ru.moore.AISUchetTehniki.services.spr.LocationService;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImportFromExcelMaterialValueOrgServiceImpl implements ImportFromExcelMaterialValueOrgService {

    private final IncomeService incomeService;
    private final LocationService locationService;

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

            CellReference cellReference = new CellReference("C2");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            Optional<Location> location = locationService.findByName(cell.getStringCellValue());
            if (location.isEmpty()){
                location = Optional.of(new Location());
                location.get().setName(cell.getStringCellValue());
                location.get().setType(LocationTypeEnum.STORAGE.getName());
                locationService.saveLocation(location.get());
            }

            IncomeDto incomeDto = new IncomeDto();
            incomeDto.setLocationId(location.get().getId());

            //проверка: есть ли данные для импорта. Если есть то, присваиваем значение
            Iterator<Row> rows = getRows(sheet);
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();


//
//                int cellIdx = 0;
//                while (cellsInRow.hasNext()) {
//                    Cell currentCell = cellsInRow.next();
//
//                    switch (cellIdx) {
//                        case 0:
//                            tutorial.setId((long) currentCell.getNumericCellValue());
//                            break;
//
//                        case 1:
//                            tutorial.setTitle(currentCell.getStringCellValue());
//                            break;
//
//                        case 2:
//                            tutorial.setDescription(currentCell.getStringCellValue());
//                            break;
//
//                        case 3:
//                            tutorial.setPublished(currentCell.getBooleanCellValue());
//                            break;
//
//                        default:
//                            break;
//                    }
//
//                    cellIdx++;
//                }
//
//                tutorials.add(tutorial);
            }

            workbook.close();
        } catch (IOException e) {
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
        String[] cellNames = new String[]{"A", "B", "C", "D"};
        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "1");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("Организация")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Организация'. Ячейка A1");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("Основание")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Основание'. Ячейка B1");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("Склад")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Склад'. Ячейка C1");
            }
            if (cellName.equals("D") && !cell.getStringCellValue().equals("МОЛ")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'МОЛ'. Ячейка D1");
            }
        }

        for (String cellName : cellNames) {
            CellReference cellReference = new CellReference(cellName + "3");
            Row row = sheet.getRow(cellReference.getRow());
            Cell cell = row.getCell(cellReference.getCol());
            if (cellName.equals("A") && !cell.getStringCellValue().equals("Наименование материальной ценности")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Наименование материальной ценности'. Ячейка A3");
            }
            if (cellName.equals("B") && !cell.getStringCellValue().equals("Стоимость")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Стоимость'. Ячейка B3");
            }
            if (cellName.equals("C") && !cell.getStringCellValue().equals("Бюджетный счет")) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Неверный формат шаблона. Нет поле 'Бюджетный счет'. Ячейка C3");
            }
        }
    }

    private void checkIsHeader(Sheet sheet) {
        CellReference cellReference = new CellReference("C2");
        Row row = sheet.getRow(cellReference.getRow());
        if (row == null) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Нет значения в поле 'Склад'. Ячейка C2");
        }
    }

    private Iterator<Row> getRows(Sheet sheet) {
        Iterator<Row> rows = sheet.iterator();

//        if (rowSize<=1){
//            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Нет строк для импорта");
//        }
        return rows;
    }
}
