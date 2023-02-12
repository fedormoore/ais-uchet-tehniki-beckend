package ru.moore.AISUchetTehniki.services.spr;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.MaterialValueOrg;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;
import ru.moore.AISUchetTehniki.models.Entity.spr.User;
import ru.moore.AISUchetTehniki.repositories.MaterialValueOrgRepository;
import ru.moore.AISUchetTehniki.repositories.spr.LocationRepository;
import ru.moore.AISUchetTehniki.repositories.spr.UserRepository;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final MaterialValueOrgRepository materialValueOrgRepository;
    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;

    public List<LocationDto> getAllLocationList(MultiValueMap<String, String> params) {
        if (!params.isEmpty()) {
            List<LocationDto> locationDtoList = mapperUtils.mapAll(locationRepository.findAllByParentIdIsNullOrderByNameDesc(), LocationDto.class);
            return filter(locationDtoList, params);
        } else {
            List<Location> locationList = locationRepository.findAllByParentIdIsNullOrderByNameDesc();
            return mapperUtils.mapAll(locationList, LocationDto.class);
        }
    }

    @Transactional
    public List<LocationDto> saveLocation(List<LocationDto> locationDtoList) {

        List<Location> returnLocationList = new ArrayList<>();
        for (LocationDto locationDto : locationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = findById(locationDto.getId()).orElse(new Location());
            location.setType(locationDto.getType());
            location.setName(locationDto.getName());
            if (locationDto.getChildren() != null) {
                location.setChildren(saveChildren(locationDto.getChildren(), location));
            }

            locationRepository.save(location);
            returnLocationList.add(location);
        }

        return mapperUtils.mapAll(returnLocationList, LocationDto.class);

    }

    private List<Location> saveChildren(List<LocationDto> locationDtoList, Location parent) {
        List<Location> returnLocationChildrenList = new ArrayList<>();
        if (locationDtoList == null) {
            return null;
        }
        for (LocationDto locationChildrenDto : locationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = findById(locationChildrenDto.getId()).orElse(new Location());
            location.setType(locationChildrenDto.getType());
            location.setName(locationChildrenDto.getName());
            if (locationChildrenDto.getChildren() != null) {
                location.setChildren(saveChildren(locationChildrenDto.getChildren(), location));
            }
            location.setParent(parent);

            locationRepository.save(location);
            returnLocationChildrenList.add(location);
        }
        return returnLocationChildrenList;
    }

    @Transactional
    public ResponseEntity<?> deleteLocation(List<LocationDto> locationDtoList) {
        for (LocationDto locationDto : locationDtoList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = findById(locationDto.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByLocationId(location.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            List<User> user = userRepository.findByLocationId(location.getId());
            if (user.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Запись используется!");
            }

            location.setDeleted(true);
            if (location.getChildren() != null) {
                deleteChildren(location.getChildren());
            }
            locationRepository.save(location);
        }

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private void deleteChildren(List<Location> locationList) {
        if (locationList == null) {
            return;
        }
        for (Location locationChildren : locationList) {
            //КОНТРОЛЬ: СУЩЕСТВОВАНИЕ ЗАПИСИ В БАЗЕ LOCATION ПО ID
            Location location = findById(locationChildren.getId()).orElse(null);

            List<MaterialValueOrg> materialValueOrg = materialValueOrgRepository.findByLocationId(location.getId());
            if (materialValueOrg.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Подчиненная запись используется!");
            }

            List<User> user = userRepository.findByLocationId(location.getId());
            if (user.size() > 0) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Подчиненная запись используется!");
            }

            location.setDeleted(true);
            if (location.getChildren() != null) {
                deleteChildren(location.getChildren());
            }
            locationRepository.save(location);
        }
    }

    public List<LocationTypeDto> getAllLocationType() {
        List<LocationTypeDto> returnLocationTypeDtoList = new ArrayList<>();
        for (LocationTypeEnum locationTypeEnum : LocationTypeEnum.values()) {
            LocationTypeDto locationTypeDto = new LocationTypeDto();

            locationTypeDto.setName(locationTypeEnum.getName());
            locationTypeDto.setNameEnum(locationTypeEnum.name());

            returnLocationTypeDtoList.add(locationTypeDto);
        }
        return mapperUtils.mapAll(returnLocationTypeDtoList, LocationTypeDto.class);
    }

    public void saveMainStorage() {
        Location location = new Location();
        location.setName("Основной склад");
        location.setType(LocationTypeEnum.STORAGE.name());
        locationRepository.save(location);
    }

    public Optional<Location> findById(UUID id) {
        if (id != null) {
            Optional<Location> locationFind = locationRepository.findById(id);
            if (locationFind.isEmpty()) {
                throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Расположение с ID " + id + " не найдено!");
            }
            return locationFind;
        } else {
            return Optional.empty();
        }
    }

    public List<LocationDto> getAllLocationTypeStorage() {
        List<LocationDto> locationDtoList = mapperUtils.mapAll(locationRepository.findAllByParentIdIsNullOrderByNameDesc(), LocationDto.class);
        return filterByType(locationDtoList, LocationTypeEnum.STORAGE.getName());
    }

    public List<LocationDto> getAllLocationTypeCabinet() {
        List<LocationDto> locationDtoList = mapperUtils.mapAll(locationRepository.findAllByParentIdIsNullOrderByNameDesc(), LocationDto.class);
        return filterByType(locationDtoList, LocationTypeEnum.CABINET.getName());
    }

    private List<LocationDto> filterByType(List<LocationDto> locationDtoListIn, String locationTypeEnum) {
        List<LocationDto> locationDtoList = new ArrayList<>();
        for (LocationDto locationDto : locationDtoListIn) {
            if (locationDto.getChildren().size() > 0) {
                LocationDto location = locationDto;
                location.setChildren(filterByType(locationDto.getChildren(), locationTypeEnum));
                if (location.getChildren().size() > 0) {
                    locationDtoList.add(locationDto);
                }
            }
            if (locationDto.getType().equals(locationTypeEnum)) {
                locationDtoList.add(locationDto);
            }

        }
        return locationDtoList;
    }

    private List<LocationDto> filter(List<LocationDto> locationDtoListIn, MultiValueMap<String, String> params) {
        List<LocationDto> locationDtoList = new ArrayList<>();
        for (LocationDto locationDto : locationDtoListIn) {
            if (locationDto.getChildren().size() > 0) {
                LocationDto location = locationDto;
                location.setChildren(filter(locationDto.getChildren(), params));
                if (location.getChildren().size() > 0) {
                    locationDtoList.add(location);
                }
            } else {
                if (params.containsKey("type") && !params.getFirst("type").isBlank()) {
                    for (int i = 0; i < params.get("type").size(); i++) {
                        if (locationDto.getType().toLowerCase().contains(params.get("type").get(i).toLowerCase())) {
                            locationDtoList.add(locationDto);
                        }
                    }
                }
                if (params.containsKey("name") && !params.getFirst("name").isBlank()) {
                    for (int i = 0; i < params.get("name").size(); i++) {
                        if (locationDto.getName().toLowerCase().contains(params.get("name").get(i).toLowerCase())) {
                            locationDtoList.add(locationDto);
                        }
                    }
                }
            }
        }
        return locationDtoList;
    }
}
