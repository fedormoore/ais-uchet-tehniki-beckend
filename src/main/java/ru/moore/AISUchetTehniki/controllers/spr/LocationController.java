package ru.moore.AISUchetTehniki.controllers.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationDto;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationTypeDto;
import ru.moore.AISUchetTehniki.services.spr.LocationService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_location")
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationService locationService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public List<LocationDto> getAllLocationList(@RequestParam MultiValueMap<String, String> params) {
        return locationService.getAllLocationList(params);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<LocationDto> saveLocation(@JsonView(View.SAVE.class) @Valid @RequestBody List<LocationDto> locationDtoList) {
        if (locationDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return locationService.saveLocationDTOList(locationDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteLocation(@JsonView(View.DELETE.class) @Valid @RequestBody List<LocationDto> locationDtoList) {
        if (locationDtoList.size() == 0) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return locationService.deleteLocation(locationDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/type_list")
    public List<LocationTypeDto> getAllLocationType() {
        return locationService.getAllLocationType();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/only_storage_list")
    public List<LocationDto> getAllLocationTypeStorage() {
        return locationService.getAllLocationTypeStorage();
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/only_cabinet_list")
    public List<LocationDto> getAllLocationTypeCabinet() {
        return locationService.getAllLocationTypeCabinet();
    }

}
