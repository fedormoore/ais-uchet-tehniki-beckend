package ru.moore.AISUchetTehniki.models.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moore.AISUchetTehniki.models.Dto.spr.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MaterialValueOrgNotChildrenDto {

    @JsonView({View.RESPONSE.class})
    private UUID id;

    @JsonView({View.RESPONSE.class})
    private String barcode;

    @JsonView({View.RESPONSE.class})
    private String status;

    @JsonView({View.RESPONSE.class})
    private OrganizationNotChildrenDto organization;

    @JsonView({View.RESPONSE.class})
    private MaterialValueDto materialValue;

    @JsonView({View.RESPONSE.class})
    private String invNumber;

    @JsonView({View.RESPONSE.class})
    private LocationNotChildrenDto location;

    @JsonView({View.RESPONSE.class})
    private BudgetAccountDto budgetAccount;

    @JsonView({View.RESPONSE.class})
    private UserDto user;

    @JsonView({View.RESPONSE.class})
    private UserDto responsible;

}
