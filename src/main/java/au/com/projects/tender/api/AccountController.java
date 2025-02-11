package au.com.projects.tender.api;

import au.com.projects.tender.api.dto.AccountRQ;
import au.com.projects.tender.api.dto.ChangeAccountInfo;
import au.com.projects.tender.api.dto.ChangePwdRQ;
import au.com.projects.tender.api.dto.LoginRQ;
import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.exception.LoginException;
import au.com.projects.tender.model.Account;
import au.com.projects.tender.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/tender/account")
@Slf4j
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        try {
            this.service.create(account);
            return ResponseEntity.ok().build();
        } catch (InsertException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AccountRQ> login(@RequestBody LoginRQ loginRQ) {
        try {
            return ResponseEntity.ok(this.service.login(loginRQ.getEmail(), loginRQ.getPwd()));
        } catch (LoginException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/changeAccountPassword")
    public ResponseEntity<AccountRQ> changeAccountPassword(@RequestBody ChangePwdRQ changePwdRQ) {
        try {
            this.service.changeAccountPassword(changePwdRQ.getEmail(), changePwdRQ.getCurrentPwd(), changePwdRQ.getNewPwd(), changePwdRQ.getNewPwdConfirmation());
            return ResponseEntity.ok().build();
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAccountsByEmailContaining")
    public ResponseEntity<List<Account>> getAccountsByEmailContaining(@RequestParam("email") String email) {
        try {
            return ResponseEntity.ok(this.service.getAccountsByEmailContaining(email));
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hasToChangePwd")
    public ResponseEntity<Boolean> hasToChangePwd(@RequestParam("email") String email) {
        try {
            return ResponseEntity.ok(this.service.hasToChangePwd(email));
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/updateAccountInformation")
    public ResponseEntity<String> updateAccountInformation(@RequestBody ChangeAccountInfo changeAccountInfo) {
        try {
            this.service.update(changeAccountInfo);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/resetAccountPwd")
    public ResponseEntity<String> resetAccountPwd(@RequestBody ChangeAccountInfo changeAccountInfo) {
        try {
            this.service.resetAccountPwd(changeAccountInfo.getEmailHashed());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestParam("emailHashed") String emailHashed) {
        try {
            this.service.delete(emailHashed);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
