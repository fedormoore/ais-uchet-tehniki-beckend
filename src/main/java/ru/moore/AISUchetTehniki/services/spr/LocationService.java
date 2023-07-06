package ru.moore.AISUchetTehniki.services.spr;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationTypeDto;
import ru.moore.AISUchetTehniki.models.Entity.spr.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationService {
    List<LocationDto> getAllLocationList(MultiValueMap<String, String> params);

    List<LocationDto> saveLocationDTOList(List<LocationDto> locationDtoList);

    Location saveLocation(Location location);

    ResponseEntity<?> deleteLocation(List<LocationDto> locationDtoList);

    List<LocationTypeDto> getAllLocationType();

    List<LocationDto> getAllLocationTypeStorage();

    List<LocationDto> getAllLocationTypeCabinet();

    Optional<Location> findById(UUID id);

    Optional<Location> findByName(String name);

    void saveMainStorage();
}
