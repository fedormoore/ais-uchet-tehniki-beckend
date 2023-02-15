package ru.moore.AISUchetTehniki.services;

import ru.moore.AISUchetTehniki.models.Entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Optional<Account> findById(UUID id);

}
