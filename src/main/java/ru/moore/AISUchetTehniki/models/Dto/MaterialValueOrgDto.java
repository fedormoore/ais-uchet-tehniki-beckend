package ru.moore.AISUchetTehniki.models.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moore.AISUchetTehniki.models.Dto.spr.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MaterialValueOrgDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'barcode' не может быть пустым.")
    private String barcode;

    @JsonView({View.RESPONSE.class})
    private String status;

    @JsonView({View.RESPONSE.class})
    private OrganizationNotChildrenDto organization;

    @JsonView({View.SAVE.class})
    private UUID organizationId;

    @JsonView({View.RESPONSE.class})
    private MaterialValueDto materialValue;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String invNumber;

    @JsonView({View.RESPONSE.class})
    private LocationNotChildrenDto location;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'locationId' не может быть пустым.")
    private UUID locationId;

    @JsonView({View.RESPONSE.class})
    private BudgetAccountDto budgetAccount;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'budgetAccountId' не может быть пустым.")
    private UUID budgetAccountId;

    @JsonView({View.RESPONSE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private UserDto user;

    @JsonView({View.SAVE.class})
    private UUID userId;

    @JsonView({View.RESPONSE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private UserDto responsible;

    @JsonView({View.SAVE.class})
    private UUID responsibleId;

    @JsonView({View.RESPONSE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MaterialValueOrgDto> children;

}
