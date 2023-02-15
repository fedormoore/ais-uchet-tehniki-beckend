package ru.moore.AISUchetTehniki.services.materialValueOrgAction;

import ru.moore.AISUchetTehniki.models.Dto.MaterialValueOrgDto;
import ru.moore.AISUchetTehniki.models.Dto.materialValueOrgAction.RemoveDeviceDto;

import java.util.List;

public interface RemoveDeviceService {

    List<MaterialValueOrgDto> saveRemoveDevice(List<RemoveDeviceDto> removeDeviceDtoList);
}
