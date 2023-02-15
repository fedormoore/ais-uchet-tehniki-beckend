package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.WriteOffDto;

import java.util.List;

public interface WriteOffService {

    List<MaterialValueOrgDto> saveWriteOff(List<WriteOffDto> writeOffDto);
}
