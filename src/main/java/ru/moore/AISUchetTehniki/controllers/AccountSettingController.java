package ru.moore.AISUchetTehniki.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.models.Dto.*;
import ru.moore.AISUchetTehniki.services.AccountSettingService;

import javax.validation.Valid;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/account_setting")
@RequiredArgsConstructor
public class AccountSettingController {

    private final AccountSettingService accountSettingService;

    @GetMapping
    public AccountSettingDto getAccountSetting() {
        return accountSettingService.getAccountSetting();
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public AccountSettingDto saveAccountSetting(@JsonView(View.SAVE.class) @Valid @RequestBody AccountSettingDto accountSettingDto) {
        return accountSettingService.saveAccountSetting(accountSettingDto);
    }

}
