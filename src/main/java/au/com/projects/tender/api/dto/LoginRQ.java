package au.com.projects.tender.api.dto;

import lombok.Data;

@Data
public class LoginRQ {

    private String email;
    private String pwd;
}
