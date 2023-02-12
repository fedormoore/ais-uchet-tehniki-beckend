package ru.moore.AISUchetTehniki.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.services.report.GenerateBarcodePdfReport;
import ru.moore.AISUchetTehniki.services.report.HistoryPdfReport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report")
@RequiredArgsConstructor
public class ReportController {

    private final GenerateBarcodePdfReport generateBarcodePdfReport;
    private final HistoryPdfReport historyPdfReport;

    @GetMapping("/generate_barcode")
    public ResponseEntity<InputStreamResource> generateBarcode(@RequestParam MultiValueMap<String, String> params) {
        int size = 1;
        if (params.containsKey("size") && !params.getFirst("size").isBlank()) {
            size = Integer.valueOf(params.getFirst("size"));
        }

        List<String> idList = new ArrayList<>();
        if (params.containsKey("device") && !params.getFirst("device").isBlank()) {
            idList = Arrays.asList(params.getFirst("device").split(","));
        }

        ByteArrayInputStream bis = generateBarcodePdfReport.createReport(size, idList);

//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

        return ResponseEntity
                .ok()
//                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/history")
    public ResponseEntity<InputStreamResource> history(@RequestParam MultiValueMap<String, String> params) {
        List<String> idList = new ArrayList<>();
        if (params.containsKey("device") && !params.getFirst("device").isBlank()) {
            idList = Arrays.asList(params.getFirst("device").split(","));
        }

        ByteArrayInputStream bis = historyPdfReport.createReport(idList);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
