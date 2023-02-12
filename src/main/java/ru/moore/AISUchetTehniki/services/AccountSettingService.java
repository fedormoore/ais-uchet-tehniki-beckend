package ru.moore.AISUchetTehniki.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.AccountSettingDto;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.models.Entity.AccountSetting;
import ru.moore.AISUchetTehniki.repositories.AccountSettingRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountSettingService {

    private final AccountSettingRepository accountSettingRepository;

    private final MapperUtils mapperUtils;

    public AccountSettingDto getAccountSetting() {
        return mapperUtils.map(accountSettingRepository.findAll().get(0), AccountSettingDto.class);
    }

    public AccountSettingDto saveAccountSetting(AccountSettingDto accountSettingDto) {
        AccountSetting accountSetting = new AccountSetting();
        if (accountSettingRepository.findAll().size() > 0) {
            accountSetting = accountSettingRepository.findAll().get(0);
        }
        accountSetting.setPreambleStatementReport(accountSettingDto.getPreambleStatementReport());
        accountSettingRepository.save(accountSetting);
        return mapperUtils.map(accountSetting, AccountSettingDto.class);
    }
}
