package au.com.projects.tender.api;

import au.com.projects.tender.api.dto.BidInformationCompleteDTO;
import au.com.projects.tender.api.dto.BidRQ;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.service.BidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/tender/bid")
@Slf4j
public class BidController {

    private final BidService service;

    public BidController(BidService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createBid(@RequestBody BidRQ bidRQ) {
        try {
            this.service.makeBid(bidRQ.getEmail(), bidRQ.getAuctionId(), bidRQ.getAutoBidRQList());
            return ResponseEntity.ok().build();
        } catch (InsertException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getBidsForAccountAndAuction")
    public ResponseEntity<List<BidInformationCompleteDTO>> getBidsForAccountAndAuction(@RequestParam("email") String emailHashed,
                                                                                       @RequestParam("auctionId") Integer auctionId) {
        try {
            return ResponseEntity.ok(this.service.getBidsForAccountAndAuction(emailHashed, auctionId));
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
