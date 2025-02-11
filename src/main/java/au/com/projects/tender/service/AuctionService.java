package au.com.projects.tender.service;

import au.com.projects.tender.api.dto.AuctionWinnersReportDTO;
import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.model.Auction;
import au.com.projects.tender.model.Auto;
import au.com.projects.tender.model.Banner;
import au.com.projects.tender.model.Bid;
import au.com.projects.tender.repository.AuctionRepository;
import au.com.projects.tender.repository.AutoRepository;
import au.com.projects.tender.repository.BidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AutoRepository autoRepository;
    private final BidRepository bidRepository;
    //path Tests: /xampp/htdocs/assets/img/AutoImages/
    //path Server: /var/www/html/assets/img/AutoImages/
    private final String autosPath = "/var/www/html/assets/img/AutoImages/";

    private final String defaultImgName = "autoImage";

    private String errorMsg;

    public AuctionService(AuctionRepository auctionRepository, AutoRepository autoRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.autoRepository = autoRepository;
        this.bidRepository = bidRepository;
    }


    public void create(Auction auction, List<Auto> auctionAutos, List<MultipartFile> imgAutos) throws InsertException {
        try {
            log.info("Create Tender");
            Auction newAuction = new Auction();
            newAuction.setCreationDate(new Date(Instant.now().toEpochMilli()));
            newAuction.setStartDate(auction.getStartDate());
            newAuction.setEndDate(auction.getEndDate());
            newAuction.setEnabled(true);
            Auction auctionSaved = this.auctionRepository.save(newAuction);
            auctionSaved.setNumber(auctionSaved.getId());
            this.auctionRepository.save(auctionSaved);
            this.checkDirectory(this.autosPath + auctionSaved.getId());
            auctionAutos.forEach(auto -> {
                Auto autoToAdd = new Auto();
                autoToAdd.setAuction(auctionSaved);
                autoToAdd.setMake(auto.getMake());
                autoToAdd.setModel(auto.getModel());
                autoToAdd.setPlateNumber(auto.getPlateNumber());
                autoToAdd.setRego(auto.getRego());
                autoToAdd.setFeatures(auto.getFeatures().replace(';', '#'));
                autoToAdd.setTransmission(auto.getTransmission());
                autoToAdd.setKms(auto.getKms());
                autoToAdd.setColor(auto.getColor());
                Auto autoSaved = this.autoRepository.save(autoToAdd);
                this.checkDirectory(this.autosPath + auctionSaved.getId() + "/" + autoSaved.getId());
                if (imgAutos != null && !imgAutos.isEmpty()) {
                    AtomicInteger contImgSaved = new AtomicInteger(0);
                    AtomicInteger contAllFiles = new AtomicInteger(0);
                    AtomicReference<String> images = new AtomicReference<>("");
                    imgAutos.forEach(file -> {
                        try {
                            contAllFiles.incrementAndGet();
                            if (file.getOriginalFilename().contains(auto.getPlateNumber())) {
                                String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                                int cont = contImgSaved.getAndIncrement();
                                String fileName = this.defaultImgName + cont + extension;
                                if (file.getOriginalFilename().equals(auto.getImgToDisplay())) {
                                    autoSaved.setImgToDisplay(fileName);
                                    this.autoRepository.save(autoSaved);
                                }
                                images.updateAndGet(currentValue -> currentValue + fileName + ",");
                                this.saveFile(file, this.autosPath + auctionSaved.getId() + "/" + autoSaved.getId() + "/" + fileName);
                            }

                            if (contAllFiles.get() == imgAutos.size()) {
                                autoSaved.setImages(images.get().substring(0, images.get().length() - 1));
                                this.autoRepository.save(autoSaved);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error creating new tender!" : this.errorMsg;
            log.error(errorMsg + " " + ex);
            throw new InsertException(Auction.class.getSimpleName(), errorMsg);
        }
    }

    public void delete(Integer id) throws Exception {
        try {
            log.info("Deleting tender: " + id);
            Optional<Auction> optionalAuction = this.auctionRepository.findById(id);
            if (optionalAuction.isEmpty()) handleNullAuction();
            assert optionalAuction.isPresent();

            Auction auctionToDelete = optionalAuction.get();
            List<Bid> auctionBids = this.bidRepository.findByAuctionId(auctionToDelete.getId());

            if (!auctionBids.isEmpty()) {
                handleBidsSubmitted("The tender cannot be deleted as it has registered bids; the system's accountability and transparency do not allow it. " +
                        "\nOnly tender deactivation is possible.");
            }

            List<Auto> auctionAutos = this.autoRepository.findByAuctionId(auctionToDelete.getId());
            auctionAutos.forEach(auto -> {
                for (String image : auto.getImages().split(",")) {
                    try {
                        this.deleteFile(this.autosPath + auctionToDelete.getId() + "/" + auto.getId() + "/" + image);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    this.deleteFile(this.autosPath + auctionToDelete.getId() + "/" + auto.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.autoRepository.delete(auto);
            });
            try {
                this.deleteFile(this.autosPath + auctionToDelete.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.auctionRepository.delete(auctionToDelete);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error deleting tender" : this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void changeTenderStatus(Integer id, boolean enabled) throws Exception {
        try {
            log.info("Changing tender status: " + id);
            Optional<Auction> optionalAuction = this.auctionRepository.findById(id);
            if (optionalAuction.isEmpty()) handleNullAuction();
            assert optionalAuction.isPresent();

            Auction auction = optionalAuction.get();
            auction.setEnabled(enabled);
            this.auctionRepository.save(auction);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error changing tender status" : this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void updateAuctionInfo(Auction auction) throws Exception {
        try {
            log.info("Changing tender status: " + auction.getId());
            Optional<Auction> optionalAuction = this.auctionRepository.findById(auction.getId());
            if (optionalAuction.isEmpty()) handleNullAuction();
            assert optionalAuction.isPresent();

            Auction auctionToUpdate = optionalAuction.get();
            auctionToUpdate.setStartDate(auction.getStartDate());
            auctionToUpdate.setEndDate(auction.getEndDate());
            this.auctionRepository.save(auctionToUpdate);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error updating tender information!" : this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void addingAutoToTender(Integer auctionId, Auto auto, List<MultipartFile> imgAutos) throws Exception {
        try {
            log.info("Adding auto to tender: " + auctionId);
            Optional<Auction> optionalAuction = this.auctionRepository.findById(auctionId);
            if (optionalAuction.isEmpty()) handleNullAuction();
            assert optionalAuction.isPresent();

            Auction auction = optionalAuction.get();
            Auto autoToAdd = new Auto();
            autoToAdd.setAuction(auction);
            autoToAdd.setMake(auto.getMake());
            autoToAdd.setModel(auto.getModel());
            autoToAdd.setPlateNumber(auto.getPlateNumber());
            autoToAdd.setRego(auto.getRego());
            autoToAdd.setFeatures(auto.getFeatures().replace(';', '#'));
            autoToAdd.setTransmission(auto.getTransmission());
            autoToAdd.setKms(auto.getKms());
            autoToAdd.setColor(auto.getColor());
            Auto autoSaved = this.autoRepository.save(autoToAdd);
            this.checkDirectory(this.autosPath + auction.getId() + "/" + autoSaved.getId());
            if (imgAutos != null && !imgAutos.isEmpty()) {
                AtomicInteger contImgSaved = new AtomicInteger(0);
                AtomicInteger contAllFiles = new AtomicInteger(0);
                AtomicReference<String> images = new AtomicReference<>("");
                imgAutos.forEach(file -> {
                    try {
                        contAllFiles.incrementAndGet();
                        if (file.getOriginalFilename().contains(auto.getPlateNumber())) {
                            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                            int cont = contImgSaved.getAndIncrement();
                            String fileName = this.defaultImgName + cont + extension;
                            if (file.getOriginalFilename().equals(auto.getImgToDisplay())) {
                                autoSaved.setImgToDisplay(fileName);
                                this.autoRepository.save(autoSaved);
                            }
                            images.updateAndGet(currentValue -> currentValue + fileName + ",");
                            this.saveFile(file, this.autosPath + auction.getId() + "/" + autoSaved.getId() + "/" + fileName);
                        }

                        if (contAllFiles.get() == imgAutos.size()) {
                            autoSaved.setImages(images.get().substring(0, images.get().length() - 1));
                            this.autoRepository.save(autoSaved);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error adding auto to tender!" : this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void updateAutoInfoFromTender(Auto auto, List<MultipartFile> imgAutos) throws Exception {
        try {
            log.info("Updating auto information");
            Optional<Auto> autoOptional = this.autoRepository.findById(auto.getId());
            if (autoOptional.isEmpty()) handleNullAuto();

            assert autoOptional.isPresent();

            Auto autoToUpdate = autoOptional.get();

            autoToUpdate.setMake(autoToUpdate.getMake().equals(auto.getMake()) ? autoToUpdate.getMake() : auto.getMake());
            autoToUpdate.setModel(autoToUpdate.getModel().equals(auto.getModel()) ? autoToUpdate.getModel() : auto.getModel());
            autoToUpdate.setRego(autoToUpdate.getRego().equals(auto.getRego()) ? autoToUpdate.getRego() : auto.getRego());
            autoToUpdate.setFeatures(autoToUpdate.getFeatures().replace(';', '#').equals(auto.getFeatures().replace(';', '#')) ? autoToUpdate.getFeatures().replace(';', '#') : auto.getFeatures().replace(';', '#'));
            autoToUpdate.setTransmission(autoToUpdate.getTransmission().equals(auto.getTransmission()) ? autoToUpdate.getTransmission() : auto.getTransmission());
            autoToUpdate.setKms(autoToUpdate.getKms().equals(auto.getKms()) ? autoToUpdate.getKms() : auto.getKms());
            autoToUpdate.setColor(autoToUpdate.getColor().equals(auto.getColor()) ? autoToUpdate.getColor() : auto.getColor());
            autoToUpdate.setImgToDisplay(autoToUpdate.getImgToDisplay().equals(auto.getImgToDisplay()) ? autoToUpdate.getImgToDisplay() : auto.getImgToDisplay());

            if (imgAutos != null && !imgAutos.isEmpty()) {
                for (String image : autoToUpdate.getImages().split(",")) {
                    try {
                        this.deleteFile(this.autosPath + autoToUpdate.getAuction().getId() + "/" + auto.getId() + "/" + image);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                AtomicInteger contImgSaved = new AtomicInteger(0);
                AtomicInteger contAllFiles = new AtomicInteger(0);
                AtomicReference<String> images = new AtomicReference<>("");
                imgAutos.forEach(file -> {
                    try {
                        contAllFiles.incrementAndGet();
                        if (file.getOriginalFilename().contains(auto.getPlateNumber())) {
                            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                            int cont = contImgSaved.getAndIncrement();
                            String fileName = this.defaultImgName + cont + extension;
                            if (file.getOriginalFilename().equals(auto.getImgToDisplay())) {
                                autoToUpdate.setImgToDisplay(fileName);
                                this.autoRepository.save(autoToUpdate);
                            }
                            images.updateAndGet(currentValue -> currentValue + fileName + ",");
                            this.saveFile(file, this.autosPath + autoToUpdate.getAuction().getId() + "/" + autoToUpdate.getId() + "/" + fileName);
                        }

                        if (contAllFiles.get() == imgAutos.size()) {
                            autoToUpdate.setImages(images.get().substring(0, images.get().length() - 1));
                            this.autoRepository.save(autoToUpdate);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            this.autoRepository.save(autoToUpdate);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error updating auto info: " + auto.getId() + ". Error: " + ex : this.errorMsg;
            log.error(errorMsg);
            throw new Exception(errorMsg);
        }
    }

    public void deleteAutoFromTender(Integer auctionId, Integer autoId) throws Exception {
        try {
            log.info("Deleting auto: " + autoId + ", from tender: " + auctionId);
            Optional<Auction> optionalAuction = this.auctionRepository.findById(auctionId);
            if (optionalAuction.isEmpty()) handleNullAuction();
            assert optionalAuction.isPresent();

            Optional<Auto> optionalAuto = this.autoRepository.findById(autoId);
            if (optionalAuto.isEmpty()) handleNullAuto();
            assert optionalAuto.isPresent();

            Auction auction = optionalAuction.get();
            Auto auto = optionalAuto.get();

            List<Bid> bids = this.bidRepository.findByAutoId(autoId);

            if (!bids.isEmpty()) {
                handleBidsSubmitted("The auto cannot be deleted as it has registered bids; the system's accountability and transparency do not allow it. " +
                        "\nThe auto cannot be deleted!");
            }

            for (String image : auto.getImages().split(",")) {
                try {
                    this.deleteFile(this.autosPath + auction.getId() + "/" + auto.getId() + "/" + image);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                this.deleteFile(this.autosPath + auction.getId() + "/" + auto.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.autoRepository.delete(auto);
        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error deleting auto from tender!" : this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public List<Auction> getActiveAuctions() throws DocumentNotFoundException {
        try {
            log.info("Getting active auctions");
            Date currentDate = new Date(Instant.now().toEpochMilli());
            List<Auction> activeAuctions = this.auctionRepository.findByStartDateBeforeAndEndDateAfter(currentDate, currentDate);
            if (activeAuctions == null || activeAuctions.isEmpty()) handleEmptyAuctions("No active auctions.");
            assert activeAuctions != null;

            return activeAuctions;
        } catch (Exception e) {
            log.error("Error, getting active auctions: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting active auctions.");
        }

    }

    public List<Auction> getFutureAuctions() throws DocumentNotFoundException {
        try {
            log.info("Getting future auctions");
            Date currentDate = new Date(Instant.now().toEpochMilli());
            List<Auction> futureAuctions = this.auctionRepository.findByStartDateAfter(currentDate);
            if (futureAuctions == null || futureAuctions.isEmpty()) handleEmptyAuctions("No future auctions.");
            assert futureAuctions != null;

            return futureAuctions;
        } catch (
                Exception e) {
            log.error("Error, getting future auctions: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting future auctions.");
        }

    }

    public List<Auction> getEnabledAuctions() throws DocumentNotFoundException {
        try {
            log.info("Getting auctions enabled");
            List<Auction> enabledAuctions = this.auctionRepository.findByEnabled(true);
            Date currentDate = new Date(Instant.now().toEpochMilli());
            List<Auction> enabledAuctionsValidated = new ArrayList<>();
            enabledAuctions.forEach(auction -> {
                if (currentDate.after(auction.getStartDate()) && currentDate.before(auction.getEndDate())) {
                    enabledAuctionsValidated.add(auction);
                }
            });
            if (enabledAuctionsValidated.isEmpty()) {
                log.error("There are no auctions currently available");
                this.errorMsg = "There are no auctions currently available";
                throw new DocumentNotFoundException(this.errorMsg);
            }
            return enabledAuctionsValidated;
        } catch (Exception e) {
            log.error("Error, getting enabled auctions: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting enabled auctions.");
        }
    }

    public List<Integer> getAllAuctionNumbers() throws DocumentNotFoundException {
        try {
            List<Integer> auctionsNumber = new ArrayList<>();
            for (Auction auction : this.auctionRepository.findAll()) {
                auctionsNumber.add(auction.getNumber());
            }

            if (auctionsNumber.isEmpty()) handleEmptyAuctions("Error no auctions registered.");

            return auctionsNumber;
        } catch (Exception e) {
            log.error("Error, getting all auctions: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting all auctions.");
        }
    }

    public List<AuctionWinnersReportDTO> getAuctionWinnersByAuctionNumber(Integer auctionNumber) throws DocumentNotFoundException {
        try {
            log.info("Getting winners of Auction N°: " + auctionNumber);
            Auction auction = auctionRepository.findByNumber(auctionNumber);
            if (auction == null) handleNullAuction();
            assert auction != null;

            Date currentDate = new Date(Instant.now().toEpochMilli());
            if (currentDate.after(auction.getEndDate())) {
                auction.setEnabled(false);
                this.auctionRepository.save(auction);
            }

            List<Auto> auctionAutos = this.autoRepository.findByAuctionId(auction.getId());

            if (auctionAutos == null || auctionAutos.isEmpty()) handleEmptyAutosInAuction();
            assert auctionAutos != null;

            List<AuctionWinnersReportDTO> auctionWinnersReportDTOS = new ArrayList<>();
            auctionAutos.forEach(auto -> {
                List<Bid> previousBidsPerAuto = this.bidRepository.findByAuctionIdAndAutoId(auction.getId(), auto.getId());
                if (previousBidsPerAuto != null && !previousBidsPerAuto.isEmpty()) {
                    Optional<Bid> highestBid = getHighestBids(previousBidsPerAuto);
                    if (highestBid.isPresent()) {
                        AuctionWinnersReportDTO auctionWinnersReportDTO = createAuctionWinnersReportDTO(auto, highestBid.get());
                        auctionWinnersReportDTOS.add(auctionWinnersReportDTO);
                    }
                } else {
                    AuctionWinnersReportDTO auctionWinnersReportDTO = createAuctionWinnersReportDTO(auto, null);
                    auctionWinnersReportDTOS.add(auctionWinnersReportDTO);
                }
            });
            return auctionWinnersReportDTOS;

        } catch (Exception e) {
            log.error("Error, getting auction winners: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting auction winners.");
        }
    }

    private AuctionWinnersReportDTO createAuctionWinnersReportDTO(Auto auto, Bid bid) {
        AuctionWinnersReportDTO auctionWinnersReportDTO = new AuctionWinnersReportDTO();
        auctionWinnersReportDTO.setAutoId(auto.getId());
        auctionWinnersReportDTO.setEmailWinner((bid != null) ? bid.getAccount().getEmail() : "-");
        auctionWinnersReportDTO.setPhoneWinner((bid != null) ? bid.getAccount().getPhone() : "-");
        auctionWinnersReportDTO.setBidAmount((bid != null) ? bid.getAmount() : 0.0f);
        auctionWinnersReportDTO.setMake(auto.getMake());
        auctionWinnersReportDTO.setModel(auto.getModel());
        auctionWinnersReportDTO.setPlateNumber(auto.getPlateNumber());
        auctionWinnersReportDTO.setImgToDisplay(auto.getImgToDisplay());
        auctionWinnersReportDTO.setRego(auto.getRego());
        auctionWinnersReportDTO.setFeatures(auto.getFeatures());
        auctionWinnersReportDTO.setTransmission(auto.getTransmission());
        auctionWinnersReportDTO.setKms(auto.getKms());
        auctionWinnersReportDTO.setColor(auto.getColor());
        auctionWinnersReportDTO.setImages(auto.getImages());
        return auctionWinnersReportDTO;
    }


    private void deleteFile(String path) throws Exception {
        File fileToDelete = new File(path);
        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            if (!deleted) {
                log.error("Error deleting file");
                throw new Exception();
            }
        } else {
            log.error("Error the file does not exist");
            throw new Exception();
        }
    }

    private void handleBidsSubmitted(String msg) {
        log.error(msg);
        this.errorMsg = msg;
        try {
            throw new Exception(this.errorMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFile(MultipartFile file, String filePath) throws IOException {
        byte[] bytes = file.getBytes();
        File newFile = new File(filePath);
        FileOutputStream fos = new FileOutputStream(newFile);
        fos.write(bytes);
        fos.close();
    }

    private void checkDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void handleEmptyAuctions(String msg) {
        log.error(msg);
        errorMsg = msg;
        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
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

    private void handleNullAuction() {
        log.error("Error, null auction.");
        errorMsg = "Error, null auction.";
        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNullAuto() {
        log.error("Error, null auto.");
        errorMsg = "Error, null auto.";
        try {
            throw new DocumentNotFoundException(errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Bid> getHighestBids(List<Bid> bids) {
        OptionalDouble maxAmount = bids.stream()
                .mapToDouble(Bid::getAmount)
                .max();

        if (maxAmount.isPresent()) {
            double highestAmount = maxAmount.getAsDouble();
            return bids.stream()
                    .filter(bid -> bid.getAmount() == highestAmount)
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }
}
