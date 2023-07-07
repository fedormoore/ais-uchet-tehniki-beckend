package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class, View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'lastName' не может быть пустым.")
    private String lastName;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotBlank(groups = {OnSave.class}, message = "Поле 'firstName' не может быть пустым.")
    private String firstName;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String middleNames;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @Email(message = "Некорректный E-mail")
    @NotBlank(groups = {OnSave.class}, message = "Поле 'email' не может быть пустым.")
    private String email;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String telephone;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String organizationFunction;

    @JsonView({View.RESPONSE.class})
    private LocationNotChildrenDto location;

    @JsonView({View.SAVE.class})
    private UUID locationId;

    @JsonView({View.RESPONSE.class})
    private OrganizationNotChildrenDto organization;

    @JsonView({View.SAVE.class})
    private UUID organizationId;

    public void setLastName(String lastName) {
        String result = lastName.replaceAll("\\s+", " ");
        this.lastName = result.trim();
    }

    public void setFirstName(String firstName) {
        String result = firstName.replaceAll("\\s+", " ");
        this.firstName = result.trim();
    }

    public void setMiddleNames(String middleNames) {
        String result = middleNames.replaceAll("\\s+", " ");
        this.middleNames = result.trim();
    }

    public void setEmail(String email) {
        String result = email.replaceAll("\\s+", " ");
        this.email = result.trim();
    }

    public void setTelephone(String telephone) {
        if (telephone != null) {
            String result = telephone.replaceAll("\\s+", " ");
            this.telephone = result.trim();
        }
    }

    public void setOrganizationFunction(String organizationFunction) {
        if (organizationFunction != null) {
            String result = organizationFunction.replaceAll("\\s+", " ");
            this.organizationFunction = result.trim();
        }
    }
}
