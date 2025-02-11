package au.com.projects.tender.api.dto;

import lombok.Data;

@Data
public class ChangeAccountInfo {

    private String emailHashed;
    private String phone;
    private Boolean enabled;
}
