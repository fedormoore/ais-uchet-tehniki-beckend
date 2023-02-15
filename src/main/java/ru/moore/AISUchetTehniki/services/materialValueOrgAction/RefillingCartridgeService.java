package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RefillingCartridgeDto;

import java.util.List;

public interface RefillingCartridgeService {

    List<MaterialValueOrgDto> saveRefillingCartridge(List<RefillingCartridgeDto> refillingCartridgeDtoList);
}
