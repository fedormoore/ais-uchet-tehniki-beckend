package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moore.AISUchetTehniki.models.Dto.View;

@Data
@NoArgsConstructor
public class LocationTypeDto {

    @JsonView({View.RESPONSE.class})
    private String name;

    @JsonView({View.RESPONSE.class})
    private String nameEnum;

}
