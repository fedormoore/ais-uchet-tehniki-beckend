package ru.moore.AISUchetTehniki.controllers.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;
import ru.moore.AISUchetTehniki.models.Dto.spr.CounterpartyDto;
import ru.moore.AISUchetTehniki.services.spr.CounterpartyService;
import ru.moore.AISUchetTehniki.specifications.CounterpartySpecifications;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/app/spr_counterparty")
@RequiredArgsConstructor
@Validated
public class CounterpartyController {

    private final CounterpartyService counterpartyService;

    @JsonView(View.RESPONSE.class)
    @GetMapping
    public Page<CounterpartyDto> allCounterparty(@RequestParam MultiValueMap<String, String> params, @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "limit", defaultValue = "20") int limit) {
        if (page < 1) {
            page = 1;
        }
        return counterpartyService.getAllCounterpartyPage(CounterpartySpecifications.build(params), page, limit);
    }

    @JsonView(View.RESPONSE.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(OnSave.class)
    public List<CounterpartyDto> saveCounterparty(@JsonView(View.SAVE.class) @Valid @RequestBody List<CounterpartyDto> counterpartyDtoList) {
        if (counterpartyDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return counterpartyService.saveCounterparty(counterpartyDtoList);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated(OnDelete.class)
    public ResponseEntity<?> deleteCounterparty(@JsonView(View.DELETE.class) @Valid @RequestBody List<CounterpartyDto> counterpartyDtoList) {
        if (counterpartyDtoList.size()==0){
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Пустой запрос.");
        }
        return counterpartyService.deleteCounterparty(counterpartyDtoList);
    }

    @JsonView(View.RESPONSE.class)
    @GetMapping("/list")
    public List<CounterpartyDto> getAllCounterpartyList() {
        return counterpartyService.getAllCounterpartyList();
    }
}
