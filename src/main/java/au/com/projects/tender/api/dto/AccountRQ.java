package au.com.projects.tender.api.dto;

import lombok.Data;

@Data
public class AccountRQ {

    private String name;
    private String email;
    private String role;
}
