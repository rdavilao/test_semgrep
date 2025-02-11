package au.com.projects.tender.service;

import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.model.Auto;
import au.com.projects.tender.repository.AutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AutoService {

    private final AutoRepository autoRepository;

    private String errorMsg;

    public AutoService(AutoRepository autoRepository) {
        this.autoRepository = autoRepository;
    }

    public List<Auto> getAutosByAuctionId(Integer auctionId) throws DocumentNotFoundException {
        try {
            List<Auto> autos = this.autoRepository.findByAuctionId(auctionId);

            if (autos == null || autos.isEmpty()) handleEmptyAutosInAuction();
            assert autos != null;
            return autos;
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error getting autos by auction id: " + auctionId : this.errorMsg;
            log.error(errorMsg);
            throw new DocumentNotFoundException(errorMsg);
        }
    }

    private void handleEmptyAutosInAuction() {
        log.error("Error, no cars are registered in the auction.");
        errorMsg = "Error, no cars are registered in the auction.";
        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
