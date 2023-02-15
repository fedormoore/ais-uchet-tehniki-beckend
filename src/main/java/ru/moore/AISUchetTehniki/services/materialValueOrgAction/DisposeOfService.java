package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.DisposeOfDto;

import java.util.List;

public interface DisposeOfService {

    List<MaterialValueOrgDto> saveDisposeOf(List<DisposeOfDto> disposeOfDtoList);
}
