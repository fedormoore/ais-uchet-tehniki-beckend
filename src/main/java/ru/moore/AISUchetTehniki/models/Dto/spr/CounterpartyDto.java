package ru.moore.AISUchetTehniki.models.Dto.spr;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.moore.AISUchetTehniki.models.Dto.OnSave;
import ru.moore.AISUchetTehniki.models.Dto.OnDelete;
import ru.moore.AISUchetTehniki.models.Dto.View;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CounterpartyDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class, View.DELETE.class})
    @NotNull(groups = {OnDelete.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    @NotEmpty(groups = {OnSave.class}, message = "Поле 'name' не может быть пустым.")
    private String name;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String inn;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String telephone;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String email;

    @JsonView({View.RESPONSE.class, View.SAVE.class})
    private String contact;

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        String result = name.replaceAll("\\s+", " ");
        this.name = result.trim();
    }

    public void setInn(String inn) {
        if (inn == null) {
            inn = "";
        }
        String result = inn.replaceAll("\\s+", " ");
        this.inn = result.trim();
    }

    public void setTelephone(String telephone) {
        if (telephone == null) {
            telephone = "";
        }
        String result = telephone.replaceAll("\\s+", " ");
        this.telephone = result.trim();
    }

    public void setEmail(String email) {
        if (email == null) {
            email = "";
        }
        String result = email.replaceAll("\\s+", " ");
        this.email = result.trim();
    }

    public void setContact(String contact) {
        if (contact == null) {
            contact = "";
        }
        String result = contact.replaceAll("\\s+", " ");
        this.contact = result.trim();
    }
}
