package au.com.projects.tender.api;

import au.com.projects.tender.api.dto.BidInformationCompleteDTO;
import au.com.projects.tender.api.dto.ChangeAccountInfo;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.model.Banner;
import au.com.projects.tender.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/tender/banner")
@Slf4j
public class BannerController {

    private final BannerService service;

    public BannerController(BannerService service) {
        this.service = service;
    }

    @PostMapping("/addImage")
    public ResponseEntity<String> addImageToCarousel(@RequestParam("isMobile") String isMobile,
                                                     @RequestParam("imgOrder") String imgOrder,
                                                     @RequestParam("img") MultipartFile img) {
        try {
            this.service.addImageToBanner(Boolean.parseBoolean(isMobile), Integer.parseInt(imgOrder), img);
            return ResponseEntity.ok().build();
        } catch (InsertException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/updateImgOrder")
    public ResponseEntity<String> updateImgOrder(@RequestBody Banner banner) {
        try {
            this.service.changeCarouselImageOrder(banner.getId(), banner.getImgOrder());
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteBannerImg")
    public ResponseEntity<String> deleteBannerImg(@RequestParam("id") Integer id) {
        try {
            this.service.deleteCarouselImage(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/getBannerImagesByDeviceType")
    public ResponseEntity<List<String>> getBannerImagesByDeviceType(@RequestParam("isMobile") Boolean isMobile) {
        try {
            return ResponseEntity.ok(this.service.getBannerImagesByDeviceType(isMobile));
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getAllBannerRecords")
    public ResponseEntity<List<Banner>> getAllBannerRecords() {
        try {
            return ResponseEntity.ok(this.service.getAllBannerRecords());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
