package au.com.projects.tender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "account", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_email"})})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false)
    private Integer id;

    @Column(name = "account_email", nullable = false, unique = true)
    private String email;

    @Column(name = "account_phone", nullable = false, unique = true, length = 16)
    private String phone;

    @Column(name = "account_email_hashed", nullable = false)
    private String emailHashed;

    @Column(name = "account_salt",nullable = false)
    private byte[] salt;

    @Column(name = "account_pwd", nullable = false)
    private String pwd;

    @Column(name = "account_role", nullable = false)
    private String role;

    @Column(name = "account_creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "account_enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "account_last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

}
