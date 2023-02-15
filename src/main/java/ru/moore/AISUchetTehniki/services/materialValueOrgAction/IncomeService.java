package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import org.springframework.security.core.Authentication;
import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.IncomeDto;

import java.util.List;

public interface IncomeService {

    List<MaterialValueOrgDto> saveIncome(IncomeDto incomeDto, Authentication authentication);
}
