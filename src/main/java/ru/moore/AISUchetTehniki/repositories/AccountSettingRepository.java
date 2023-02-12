package ru.moore.AISUchetTehniki.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.moore.AISUchetTehniki.models.Entity.AccountSetting;

import java.util.UUID;

@Repository
public interface AccountSettingRepository extends JpaRepository<AccountSetting, UUID> {

}
