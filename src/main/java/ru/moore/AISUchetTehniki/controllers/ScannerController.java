package ru.moore.AISUchetTehniki.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.security.UserPrincipal;
import ru.moore.AISUchetTehniki.services.MaterialValueOrgService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/scanner")
@RequiredArgsConstructor
public class ScannerController {

    private final MaterialValueOrgService materialValueOrgService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/{code}")
    public void scan(@PathVariable String code, Authentication authentication) {
        send(code, authentication);
    }

    public void send(String code, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        messagingTemplate.convertAndSend("/topic/scanner/"+userPrincipal.getEmail(), materialValueOrgService.toDtoRegistry(materialValueOrgService.findByBarcode(code)));
    }

}
