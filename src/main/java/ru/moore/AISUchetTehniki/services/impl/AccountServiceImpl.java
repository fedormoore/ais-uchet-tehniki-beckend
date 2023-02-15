package ru.moore.AISUchetTehniki.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.repositories.AccountRepository;
import ru.moore.AISUchetTehniki.services.AccountService;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> findById(UUID id) {
        if (id != null) {
            Optional<Account> accountFind = accountRepository.findById(id);
            if (accountFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Учетная запись с ID "+id+" не найдено!");
            }
            return accountFind;
        } else {
            return Optional.empty();
        }
    }
}
