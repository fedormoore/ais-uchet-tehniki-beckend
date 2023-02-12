package ru.moore.AISUchetTehniki.controllers.report.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.moore.AISUchetTehniki.services.report.spr.SprUserPdfReport;

import java.io.ByteArrayInputStream;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report/spr_user")
@RequiredArgsConstructor
public class SprUserReportController {

    private final SprUserPdfReport sprUserPdfReport;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateBarcode() {
        ByteArrayInputStream bis = sprUserPdfReport.createReport();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
