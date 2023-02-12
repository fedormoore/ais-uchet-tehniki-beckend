package ru.moore.AISUchetTehniki.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.services.report.ContractPdfReport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report/contract")
@RequiredArgsConstructor
public class ContractReportController {

    private final ContractPdfReport contractPdfReport;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam MultiValueMap<String, String> params) {
        List<UUID> idContract = new ArrayList<>();
        if (params.containsKey("idContract") && !params.getFirst("idContract").isBlank()) {
            for (String param : Arrays.asList(params.getFirst("idContract").split(","))) {
                idContract.add(UUID.fromString(param));
            }
        }

        ByteArrayInputStream bis = contractPdfReport.createReport(idContract);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
