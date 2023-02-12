package ru.moore.AISUchetTehniki.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.services.report.StatementPdfReport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report/statement")
@RequiredArgsConstructor
public class StatementReportController {

    private final StatementPdfReport statementPdfReport;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam MultiValueMap<String, String> params) {
        List<UUID> idStatement = new ArrayList<>();
        if (params.containsKey("idStatement") && !params.getFirst("idStatement").isBlank()) {
            for (String param : Arrays.asList(params.getFirst("idStatement").split(","))) {
                idStatement.add(UUID.fromString(param));
            }
        }

        ByteArrayInputStream bis = statementPdfReport.createReport(idStatement);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
