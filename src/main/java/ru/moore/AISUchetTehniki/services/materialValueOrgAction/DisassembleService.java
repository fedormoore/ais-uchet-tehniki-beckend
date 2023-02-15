package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.DisassembleDto;

import java.util.List;

public interface DisassembleService {

    List<MaterialValueOrgDto> saveDisassemble(List<DisassembleDto> disassembleDtoList);
}
