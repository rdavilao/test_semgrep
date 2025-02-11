package au.perth_latino_chamber.mailForwarder1.api.dto;

import lombok.Data;

@Data
public class EmailDTO {

        private String name;
        private String email;
        private String message;

    @Override
    public String toString() {
        return "Email Info{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
