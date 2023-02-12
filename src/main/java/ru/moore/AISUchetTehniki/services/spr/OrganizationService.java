package ru.moore.AISUchetTehniki.services.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.enums.OrganizationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.OrganizationTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.Organization;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.repositories.spr.OrganizationRepository;
import ru.moore.AISUchetTehniki.repositories.spr.UserRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;

    public List<OrganizationDto> getAllOrganizationList(MultiValueMap<String, String> params) {
        if (!params.isEmpty()) {
            List<OrganizationDto> organizationDtoList = mapperUtils.mapAll(organizationRepository.findAllByParentIdIsNullOrderByNameDesc(), OrganizationDto.class);
            return filter(organizationDtoList, params);
        } else {
            List<Organization> organizationList = organizationRepository.findAllByParentIdIsNullOrderByNameDesc();
            return mapperUtils.mapAll(organizationList, OrganizationDto.class);
        }
    }

    @Transactional
    public List<OrganizationDto> saveOrganization(List<OrganizationDto> organizationDtoList) {
        List<Organization> returnOrganizationList = new ArrayList<>();
        for (OrganizationDto organizationDto : organizationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ ORGANIZATION ПО ID
            Organization organization = findById(organizationDto.getId()).orElse(new Organization());
            organization.setType(organizationDto.getType());
            organization.setName(organizationDto.getName());
            if (organizationDto.getChildren() != null) {
                organization.setChildren(saveChildren(organizationDto.getChildren(), organization));
            }
            organizationRepository.save(organization);
            returnOrganizationList.add(organization);
        }

        return mapperUtils.mapAll(returnOrganizationList, OrganizationDto.class);
    }

    private List<Organization> saveChildren(List<OrganizationDto> organizationDtoList, Organization parent) {
        List<Organization> returnOrganizationChildrenList = new ArrayList<>();
        if (organizationDtoList == null) {
            return null;
        }
        for (OrganizationDto organizationChildrenDto : organizationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Organization organization = findById(organizationChildrenDto.getId()).orElse(new Organization());
            organization.setType(organizationChildrenDto.getType());
            organization.setName(organizationChildrenDto.getName());
            if (organizationChildrenDto.getChildren() != null) {
                organization.setChildren(saveChildren(organizationChildrenDto.getChildren(), organization));
            }
            organization.setParent(parent);

            organizationRepository.save(organization);
            returnOrganizationChildrenList.add(organization);
        }
        return returnOrganizationChildrenList;
    }

    @Transactional
    public ResponseEntity<?> deleteOrganization(List<OrganizationDto> organizationDtoList) {
        for (OrganizationDto organizationDto : organizationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Organization organization = findById(organizationDto.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByOrganizationId(organization.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            List<User> user = userRepository.findByOrganizationId(organization.getId());
            if (user.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            organization.setDeleted(true);
            if (organization.getChildren() != null) {
                deleteChildren(organization.getChildren());
            }
            organizationRepository.save(organization);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private void deleteChildren(List<Organization> organizationList) {
        if (organizationList == null) {
            return;
        }
        for (Organization organizationChildren : organizationList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Organization organization = findById(organizationChildren.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByOrganizationId(organization.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Подчиненная запись используется!");
            }

            List<User> user = userRepository.findByOrganizationId(organization.getId());
            if (user.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Подчиненная запись используется!");
            }

            organization.setDeleted(true);
            if (organization.getChildren() != null) {
                deleteChildren(organization.getChildren());
            }
            organizationRepository.save(organization);
        }
    }

    public List<OrganizationTypeDto> getAllOrganizationType() {
        List<OrganizationTypeDto> returnOrganizationTypeDtoList = new ArrayList<>();
        for (OrganizationTypeEnum organizationTypeEnum : OrganizationTypeEnum.values()) {
            OrganizationTypeDto organizationTypeDto = new OrganizationTypeDto();

            organizationTypeDto.setName(organizationTypeEnum.getName());
            organizationTypeDto.setNameEnum(organizationTypeEnum.name());

            returnOrganizationTypeDtoList.add(organizationTypeDto);
        }
        return mapperUtils.mapAll(returnOrganizationTypeDtoList, OrganizationTypeDto.class);
    }

    public List<OrganizationDto> getAllOrganizationTypeOrg() {
        List<String> stringList = new ArrayList<>();
        stringList.add(OrganizationTypeEnum.ORGANIZATION.name());
        stringList.add(OrganizationTypeEnum.SUBORDINATE.name());
        stringList.add(OrganizationTypeEnum.BRANCH.name());
        List<Organization> organizationList = organizationRepository.findAllByParentIdIsNullAndTypeIn(stringList);
        return mapperUtils.mapAll(organizationList, OrganizationDto.class);
    }

    public List<OrganizationDto> getAllStructureTypeStr() {
        List<OrganizationDto> organizationDtoList = mapperUtils.mapAll(organizationRepository.findAllByParentIdIsNullOrderByNameDesc(), OrganizationDto.class);
        return filterByType(organizationDtoList, OrganizationTypeEnum.STRUCTURE.getName());
    }

    public Optional<Organization> findById(UUID id) {
        if (id != null) {
            Optional<Organization> organizationFind = organizationRepository.findById(id);
            if (organizationFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Организация с ID " + id + " не найдена!");
            }
            return organizationFind;
        } else {
            return Optional.empty();
        }
    }

    private List<OrganizationDto> filterByType(List<OrganizationDto> organizationDtoListIn, String organizationTypeEnum) {
        List<OrganizationDto> organizationDtoList = new ArrayList<>();
        for (OrganizationDto organizationDto : organizationDtoListIn) {
            if (organizationDto.getChildren().size() > 0) {
                OrganizationDto organization = organizationDto;
                organization.setChildren(filterByType(organizationDto.getChildren(), organizationTypeEnum));
                if (organization.getChildren().size() > 0) {
                    organizationDtoList.add(organization);
                }
            }
            if (organizationDto.getType().equals(organizationTypeEnum)) {
                organizationDtoList.add(organizationDto);
            }

        }
        return organizationDtoList;
    }

    private List<OrganizationDto> filter(List<OrganizationDto> organizationDtoListIn, MultiValueMap<String, String> params) {
        List<OrganizationDto> organizationDtoList = new ArrayList<>();
        for (OrganizationDto organizationDto : organizationDtoListIn) {
            if (organizationDto.getChildren().size() > 0) {
                OrganizationDto organization = organizationDto;
                organization.setChildren(filter(organizationDto.getChildren(), params));
                if (organization.getChildren().size() > 0) {
                    organizationDtoList.add(organization);
                }
            } else {
                if (params.containsKey("type") && !params.getFirst("type").isBlank()) {
                    for (int i = 0; i < params.get("type").size(); i++) {
                        if (organizationDto.getType().toLowerCase().contains(params.get("type").get(i).toLowerCase())) {
                            organizationDtoList.add(organizationDto);
                        }
                    }
                }
                if (params.containsKey("name") && !params.getFirst("name").isBlank()) {
                    for (int i = 0; i < params.get("name").size(); i++) {
                        if (organizationDto.getName().toLowerCase().contains(params.get("name").get(i).toLowerCase())) {
                            organizationDtoList.add(organizationDto);
                        }
                    }
                }
            }
        }
        return organizationDtoList;
    }

}
