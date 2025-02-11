package au.com.projects.tender.api;

import au.com.projects.tender.model.Auto;
import au.com.projects.tender.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/tender/auto")
@Slf4j
public class AutoController {

    private final AutoService service;

    public AutoController(AutoService service) {
        this.service = service;
    }

    @GetMapping("/getAutosByAuction")
    public ResponseEntity<List<Auto>> getAutosByAuction(@RequestParam("auctionId") Integer auctionId) {
        try {
            return ResponseEntity.ok(this.service.getAutosByAuctionId(auctionId));
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
