package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RepairDto;

import java.util.List;

public interface RepairService {

    List<MaterialValueOrgDto> saveRepair(List<RepairDto> repairDtoList);
}
