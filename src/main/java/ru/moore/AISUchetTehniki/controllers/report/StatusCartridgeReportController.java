package ru.moore.AISUchetTehniki.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.enums.RegistryStatusEnum;
import ru.moore.AISUchetTehniki.services.report.StatusCartridgePdfReport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report/status_cartridge")
@RequiredArgsConstructor
public class StatusCartridgeReportController {

    private final StatusCartridgePdfReport statusCartridgePdfReport;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam MultiValueMap<String, String> params) {
        List<String> status = new ArrayList<>();
        if (params.containsKey("status") && !params.getFirst("status").isBlank()) {
            for (String param : Arrays.asList(params.getFirst("status").split(","))) {
                status.add(RegistryStatusEnum.convertToDatabaseColumn(param));
            }
        }

        ByteArrayInputStream bis = statusCartridgePdfReport.createReport(status);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
