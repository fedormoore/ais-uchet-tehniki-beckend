package ru.moore.AISUchetTehniki.models.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MaterialValueOrgHistoryDto {

    @JsonView({View.RESPONSE.class, View.SAVE.class, View.DELETE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'id' не может быть пустым.")
    private UUID id;

    @JsonView({View.RESPONSE.class})
    private MaterialValueOrgDto materialValueOrg;

    @JsonView({View.RESPONSE.class})
    private Date dateCreate;

    @JsonView({View.RESPONSE.class})
    private String type;

    @JsonView({View.RESPONSE.class})
    private MaterialValueOrgHistoryReasonDto reason;

    @JsonView({View.SAVE.class})
    @NotNull(groups = {OnSave.class}, message = "Поле 'reasonId' не может быть пустым.")
    private UUID reasonId;

    @JsonView({View.RESPONSE.class})
    private String oldValue;

    @JsonView({View.RESPONSE.class})
    private String newValue;

    @JsonView({View.RESPONSE.class})
    private String note;

    @JsonView({View.RESPONSE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private DeviceHistoryParentDto parent;

    @JsonView({View.RESPONSE.class})
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DeviceHistoryChildrenDto> children;

    @Data
    @NoArgsConstructor
    @SuperBuilder
    public static class MaterialValueOrgHistoryReasonDto {

        private UUID id;
        private Date date;
        private String number;
        private double sum;
//            private UUID organization;
    }

    @Data
    @NoArgsConstructor
    public static class DeviceHistoryParentDto {

        private UUID id;

        @JsonView({View.RESPONSE.class})
        private String type;

        @JsonView({View.RESPONSE.class})
        private MaterialValueOrgHistoryReasonDto reason;

        @JsonView({View.RESPONSE.class})
        private MaterialValueOrgDto materialValueOrg;

        @JsonView({View.RESPONSE.class})
        private DeviceHistoryParentDto parent;

    }

    @Data
    @NoArgsConstructor
    public static class DeviceHistoryChildrenDto {

        private UUID id;

        @JsonView({View.RESPONSE.class})
        private String type;

        @JsonView({View.RESPONSE.class})
        private MaterialValueOrgHistoryReasonDto reason;

        @JsonView({View.RESPONSE.class})
        private MaterialValueOrgDto materialValueOrg;

    }

}
