package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.ReplacementCartridgeDto;

import java.util.List;

public interface ReplacementCartridgeService {

    List<MaterialValueOrgDto> saveReplacementCartridge(List<ReplacementCartridgeDto> replacementCartridgeDtoList);
}
