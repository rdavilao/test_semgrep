package au.perth_latino_chamber.system.service;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Business;
import au.perth_latino_chamber.system.repository.BusinessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BusinessService {

    private final BusinessRepository businessRepository;

    private String errorMsg;

    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public List<Business> getAllAvaibleBusiness() throws DocumentNotFoundException {
        try {
            log.info("Getting business information");

            List<Business> businesses = this.businessRepository.findByEnabled(true);

            if (businesses.isEmpty()) {
                this.errorMsg = "There is no information from any business available.";
                log.error(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }

            return businesses;
        } catch (Exception ex) {
            log.error("Error, getting all business information: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting all business information.");
        }
    }

    public Business getBusinessInformationById(int id) throws DocumentNotFoundException {
        try {
            log.info("Getting business information by id: " + id);
            Optional<Business> optionalBusiness = this.businessRepository.findById(id);
            if (!optionalBusiness.isPresent()) {
                this.errorMsg = "Bussines id: " + id + " doesn't exists.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }
            assert optionalBusiness.isPresent();

            return optionalBusiness.get();
        } catch (Exception ex) {
            log.error("Error, getting business information by id: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting business information by id.");
        }

    }

}
