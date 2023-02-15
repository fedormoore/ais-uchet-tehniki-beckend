package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.ReplacementDto;

import java.util.List;

public interface ReplacementService {

    List<MaterialValueOrgDto> saveReplacement(List<ReplacementDto> replacementDtoList);
}
