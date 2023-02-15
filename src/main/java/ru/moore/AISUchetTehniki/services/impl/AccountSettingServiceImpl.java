package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.models.Dto.AccountSettingDto;
import ru.moore.AISUchetTehniki.models.Entity.AccountSetting;
import ru.moore.AISUchetTehniki.repositories.AccountSettingRepository;
import ru.moore.AISUchetTehniki.services.AccountSettingService;
import ru.moore.AISUchetTehniki.utils.MapperUtils;

@Service
@RequiredArgsConstructor
public class AccountSettingServiceImpl implements AccountSettingService {

    private final AccountSettingRepository accountSettingRepository;

    private final MapperUtils mapperUtils;

    @Override
    public AccountSettingDto getAccountSetting() {
        return mapperUtils.map(accountSettingRepository.findAll().get(0), AccountSettingDto.class);
    }

    @Override
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
