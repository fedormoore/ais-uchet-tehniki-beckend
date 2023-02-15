package ru.moore.AISUchetTehniki.services;

import ru.moore.AISUchetTehniki.models.Dto.AccountSettingDto;

public interface AccountSettingService {

    AccountSettingDto getAccountSetting();

    AccountSettingDto saveAccountSetting(AccountSettingDto accountSettingDto);
}
