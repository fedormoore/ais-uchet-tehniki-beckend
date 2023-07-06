package ru.moore.AISUchetTehniki.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.services.ImportFromExcelMaterialValueOrgService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api/v1/app/import/material_value_org")
@RequiredArgsConstructor
@Tag(name = "Название контроллера: ImportFromExcel", description = "Контроллер служит для добавления нового оборудования путем импорта из Excel файла")
public class ImportFromExcel {

    private final ImportFromExcelMaterialValueOrgService importFromExcelMaterialValueOrgService;

    @Operation(
            summary = "Добавление нового оборудования путем импорта из Excel файла",
            description = "Позволяет добавить новое оборудование путем импорта из Excel файла"
    )
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> ImportFromExcel(@RequestPart("file") @Parameter(description = "Excel файл. Параметр -file") MultipartFile file, Authentication authentication) {
        return importFromExcelMaterialValueOrgService.importFromExcel(file, authentication);
    }
}
