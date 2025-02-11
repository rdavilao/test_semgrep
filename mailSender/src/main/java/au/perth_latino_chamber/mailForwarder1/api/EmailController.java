package au.perth_latino_chamber.mailForwarder1.api;

import au.perth_latino_chamber.mailForwarder1.api.dto.EmailDTO;
import au.perth_latino_chamber.mailForwarder1.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/perth-latino-chamber/mail")
@Slf4j
public class EmailController {

    private final EmailService service;

    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO emailDTO) {
        try {
            this.service.sendEmail(emailDTO);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
