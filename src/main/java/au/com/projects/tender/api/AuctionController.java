package au.com.projects.tender.api;

import au.com.projects.tender.api.dto.AuctionWinnersReportDTO;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.model.Auction;
import au.com.projects.tender.model.Auto;
import au.com.projects.tender.service.AuctionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/tender/auction")
@Slf4j
public class AuctionController {

    private final AuctionService service;

    public AuctionController(AuctionService service) {
        this.service = service;
    }


    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestParam("auction") String auction,
                                         @RequestParam("auctionAutos") String auctionAutos,
                                         @RequestParam("imgAutos") List<MultipartFile> imgAutos) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Auction tender = objectMapper.readValue(auction, Auction.class);
            List<Auto> auctionAutosObj = objectMapper.readValue(auctionAutos, new TypeReference<List<Auto>>() {
            });
            this.service.create(tender,
                    auctionAutosObj, imgAutos);
            return ResponseEntity.ok().build();
        } catch (InsertException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/updateAutoInfoFromTender")
    public ResponseEntity<String> updateAutoInfoFromTender(@RequestParam("auto") String auto,
                                                           @RequestParam(value = "imgAutos", required = false) List<MultipartFile> imgAutos) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Auto autoToUpdate = objectMapper.readValue(auto, Auto.class);
            this.service.updateAutoInfoFromTender(autoToUpdate,imgAutos);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/addAutoToTender")
    public ResponseEntity<String> addAutoToTender(@RequestParam("auctionId") String auctionId,
                                                  @RequestParam("auto") String auto,
                                                  @RequestParam("imgAutos") List<MultipartFile> imgAutos) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Auto autoToAdd = objectMapper.readValue(auto, Auto.class);
            int id = Integer.parseInt(auctionId);
            this.service.addingAutoToTender(id, autoToAdd, imgAutos);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteTender")
    public ResponseEntity<String> deleteAccount(@RequestParam("id") Integer id) {
        try {
            this.service.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteAutoFromTender")
    public ResponseEntity<String> deleteTAutoFromTender(@RequestParam("auctionId") Integer auctionId,
                                                        @RequestParam("autoId") Integer autoId) {
        try {
            this.service.deleteAutoFromTender(auctionId,autoId);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PatchMapping("/updateTenderInfo")
    public ResponseEntity<String> updateTenderInfo(@RequestBody Auction auction) {
        try {
            this.service.updateAuctionInfo(auction);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/changeTenderStatus")
    public ResponseEntity<String> changeTenderStatus(@RequestBody Auction auction) {
        try {
            this.service.changeTenderStatus(auction.getId(), auction.getEnabled());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getEnabledAuctions")
    public ResponseEntity<List<Auction>> getEnabledAuctions() {
        try {
            return ResponseEntity.ok(this.service.getEnabledAuctions());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getActiveAuctions")
    public ResponseEntity<List<Auction>> getActiveAuctions() {
        try {
            return ResponseEntity.ok(this.service.getActiveAuctions());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getFutureAuctions")
    public ResponseEntity<List<Auction>> getFutureAuctions() {
        try {
            return ResponseEntity.ok(this.service.getFutureAuctions());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllAuctionNumbers")
    public ResponseEntity<List<Integer>> getAllAuctions() {
        try {
            return ResponseEntity.ok(this.service.getAllAuctionNumbers());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAuctionWinnersByAuctionNumber")
    public ResponseEntity<List<AuctionWinnersReportDTO>> getAuctionWinnersByAuctionNumber(@RequestParam("auctionNumber") Integer auctionNumber) {
        try {
            return ResponseEntity.ok(this.service.getAuctionWinnersByAuctionNumber(auctionNumber));
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
