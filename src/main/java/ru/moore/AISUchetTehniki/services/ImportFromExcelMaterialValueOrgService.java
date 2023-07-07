package ru.moore.AISUchetTehniki.services;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface ImportFromExcelMaterialValueOrgService {

    /**
     * Метод позволяет добавлять новое оборудование путем импорта из Excel файла
     * @param file Excel файл
     * @return Возвращает текстовое сообщение о статусе импорта
     */
    public ResponseEntity<String> importFromExcel(MultipartFile file, Authentication authentication);
}
