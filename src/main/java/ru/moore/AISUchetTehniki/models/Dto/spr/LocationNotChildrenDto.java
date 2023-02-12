package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moore.AISUchetTehniki.models.Dto.View;

import java.util.UUID;

@Data
@NoArgsConstructor
public class LocationNotChildrenDto {

    @JsonView({View.RESPONSE.class})
    private UUID id;

    @JsonView({View.RESPONSE.class})
    private String type;

    @JsonView({View.RESPONSE.class})
    private String name;

}
