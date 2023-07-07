package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationService {
    List<OrganizationDto> getAllOrganizationList(MultiValueMap<String, String> params);

    List<OrganizationDto> saveOrganizationDTOList(List<OrganizationDto> organizationDtoList);

    Organization saveOrganization(Organization organization);

    ResponseEntity<?> deleteOrganization(List<OrganizationDto> organizationDtoList);

    List<OrganizationTypeDto> getAllOrganizationType();

    List<OrganizationDto> getAllOrganizationTypeOrg();

    List<OrganizationDto> getAllStructureTypeStr();

    Optional<Organization> findById(UUID id);

    Optional<Organization> findByName(String name);
}
