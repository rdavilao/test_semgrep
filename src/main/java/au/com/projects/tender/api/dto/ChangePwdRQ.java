package au.com.projects.tender.api.dto;

import lombok.Data;

@Data
public class ChangePwdRQ {

    private String email;
    private String currentPwd;
    private String newPwd;
    private String newPwdConfirmation;

}
