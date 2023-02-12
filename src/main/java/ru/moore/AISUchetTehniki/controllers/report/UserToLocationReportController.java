package ru.moore.AISUchetTehniki.controllers.report;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.services.report.UserToLocationPdfReport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/report/user_to_location")
@RequiredArgsConstructor
public class UserToLocationReportController {

    private final UserToLocationPdfReport userToLocationPdfReport;

    @GetMapping
    public ResponseEntity<InputStreamResource> generateReport(@RequestParam MultiValueMap<String, String> params) {
        List<UUID> idLocation = new ArrayList<>();
        if (params.containsKey("idLocation") && !params.getFirst("idLocation").isBlank()) {
            for (String param : Arrays.asList(params.getFirst("idLocation").split(","))) {
                idLocation.add(UUID.fromString(param));
            }
        }

        ByteArrayInputStream bis = userToLocationPdfReport.createReport(idLocation);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

}
