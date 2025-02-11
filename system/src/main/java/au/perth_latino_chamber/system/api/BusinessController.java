package au.perth_latino_chamber.system.api;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Business;
import au.perth_latino_chamber.system.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/perth-latino-chamber/business")
@Slf4j
public class BusinessController {

    private final BusinessService businessService;


    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/getAllBusinessInfo")
    public ResponseEntity<List<Business>> getAllBusinessInfo() {
        try {
            return ResponseEntity.ok(this.businessService.getAllAvaibleBusiness());
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getBusinessInfo")
    public ResponseEntity<Business> getBusinessInfoById(@RequestParam("businessId") Integer businessId) {
        try {
            return ResponseEntity.ok(this.businessService.getBusinessInformationById(businessId));
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
