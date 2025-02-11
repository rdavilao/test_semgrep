package au.com.projects.tender.service;

import au.com.projects.tender.api.dto.AutoBidRQ;
import au.com.projects.tender.api.dto.BidInformationCompleteDTO;
import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.exception.LoginException;
import au.com.projects.tender.model.Account;
import au.com.projects.tender.model.Auction;
import au.com.projects.tender.model.Auto;
import au.com.projects.tender.model.Bid;
import au.com.projects.tender.repository.AccountRepository;
import au.com.projects.tender.repository.AuctionRepository;
import au.com.projects.tender.repository.AutoRepository;
import au.com.projects.tender.repository.BidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AccountRepository accountRepository;
    private final AuctionRepository auctionRepository;
    private final AutoRepository autoRepository;
    private String errorMsg;

    public BidService(BidRepository bidRepository, AccountRepository accountRepository, AuctionRepository auctionRepository, AutoRepository autoRepository) {
        this.bidRepository = bidRepository;
        this.accountRepository = accountRepository;
        this.auctionRepository = auctionRepository;
        this.autoRepository = autoRepository;
    }

    public void makeBid(String emailHashed, Integer auctionId, List<AutoBidRQ> autoBidsRQ) throws InsertException {
        try {
            log.info("Making new bid");

            Account account = accountRepository.findByEmailHashed(emailHashed);

            if (account == null) handleNullAccount(emailHashed);
            assert account != null;

            if (!account.getEnabled()) handleNotEnabledAccount();

            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> handleEntityNotFound(Auction.class, auctionId));

            log.info("Bid from: " + account.getEmail() + ", at auction number: " + auction.getNumber());

            if (autoBidsRQ == null || autoBidsRQ.isEmpty()) handleEmptyBids();
            assert autoBidsRQ != null;

            Date actualDate = new Date(Instant.now().toEpochMilli());
            if (!auction.getEnabled()) handleNotEnableAuction();

            if (!(actualDate.after(auction.getStartDate()) && actualDate.before(auction.getEndDate())))
                handleNotEnableAuction();


            handleCorrectAutoIds(autoBidsRQ);
            validateBidAmount(autoBidsRQ);
            validateMaxBidsAmount(account.getId(), auctionId, autoBidsRQ);

            Date bidDate = new Date(Instant.now().toEpochMilli());

            autoBidsRQ.forEach(autoBidRQ -> {
                Optional<Auto> auto = this.autoRepository.findById(autoBidRQ.getAutoId());
                Bid newBid = new Bid();
                newBid.setBidDate(bidDate);
                newBid.setAccount(account);
                newBid.setAuction(auction);
                newBid.setAuto(auto.get());
                newBid.setAmount(autoBidRQ.getAmount());
                bidRepository.save(newBid);
            });

        } catch (Exception e) {
            log.error("Making new bid failed: " + e);
            throw new InsertException(Account.class.getSimpleName(), errorMsg != null ? errorMsg : "Error when making a new bid!");
        }
    }

    public List<BidInformationCompleteDTO> getBidsForAccountAndAuction(String emailHashed, Integer auctionId) throws DocumentNotFoundException {
        try {
            log.info("Getting bids");

            Account account = accountRepository.findByEmailHashed(emailHashed);

            if (account == null) handleNullAccount(emailHashed);
            assert account != null;

            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> handleEntityNotFound(Auction.class, auctionId));


            log.info("User: " + account.getEmail() + "\n Auction N: " + auction.getNumber());
            List<Auto> auctionAutos = this.autoRepository.findByAuctionId(auction.getId());

            if (auctionAutos == null || auctionAutos.isEmpty()) handleEmptyAutosInAuction();
            assert auctionAutos != null;

            List<BidInformationCompleteDTO> bidInformationCompleteDTOList = new ArrayList<>();

            auctionAutos.forEach(auto -> {
                BidInformationCompleteDTO bidInformationCompleteDTO = new BidInformationCompleteDTO();
                bidInformationCompleteDTO.setAutoId(auto.getId());
                bidInformationCompleteDTO.setMake(auto.getMake());
                bidInformationCompleteDTO.setModel(auto.getModel());
                bidInformationCompleteDTO.setPlateNumber(auto.getPlateNumber());
                bidInformationCompleteDTO.setImgToDisplay(auto.getImgToDisplay());
                bidInformationCompleteDTO.setRego(auto.getRego());
                bidInformationCompleteDTO.setFeatures(auto.getFeatures());
                bidInformationCompleteDTO.setTransmission(auto.getTransmission());
                bidInformationCompleteDTO.setKms(auto.getKms());
                bidInformationCompleteDTO.setColor(auto.getColor());
                bidInformationCompleteDTO.setImages(auto.getImages());
                bidInformationCompleteDTO.setBidAmount(0.0f);
                List<Bid> previousBidsPerAuto = this.bidRepository.findByAccountIdAndAuctionIdAndAutoId(account.getId(), auctionId, auto.getId());
                if (previousBidsPerAuto != null && !previousBidsPerAuto.isEmpty()) {
                    Bid highestBid = getHighestBid(previousBidsPerAuto);
                    bidInformationCompleteDTO.setBidAmount(highestBid.getAmount());
                }
                bidInformationCompleteDTOList.add(bidInformationCompleteDTO);
            });

            return bidInformationCompleteDTOList;

        } catch (Exception e) {
            log.error("Error, getting bids failed: " + e);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting bids failed.");
        }
    }

    private <T> RuntimeException handleEntityNotFound(Class<T> entityClass, Object entityId) {
        log.error("Error, " + entityClass.getSimpleName() + " does not exist: " + entityId);
        errorMsg = "Error, " + entityClass.getSimpleName() + " does not exist";
        try {
            throw new InsertException(entityClass.getSimpleName(), errorMsg);
        } catch (InsertException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNotEnabledAccount() {
        log.error("Error, account is not enabled.");
        errorMsg = "Error, account is not enabled.";
        try {
            throw new LoginException(Account.class.getSimpleName(), errorMsg);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCorrectAutoIds(List<AutoBidRQ> autoBidsRQ) {
        autoBidsRQ.forEach(autoBidRQ -> autoRepository.findById(autoBidRQ.getAutoId())
                .orElseThrow(() -> handleEntityNotFound(Auto.class, autoBidRQ.getAutoId())));
    }

    private void handleNullAccount(String emailHashed) throws InsertException {
        log.error("Error, " + Account.class.getSimpleName() + " does not exist: " + emailHashed);
        errorMsg = "Error, " + Account.class.getSimpleName() + " does not exist";
        throw new InsertException(Account.class.getSimpleName(), errorMsg);
    }

    private void handleEmptyBids() {
        log.error("Error, bids received are empty.");
        errorMsg = "Error, bids received are empty.";
        try {
            throw new InsertException(Bid.class.getSimpleName(), errorMsg);
        } catch (InsertException e) {
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

    private void handleNotEnableAuction() {
        log.error("Error, auction are not enabled or is out of range of valid days.");
        errorMsg = "Error, auction are not enabled or is out of range of valid days.";
        try {
            throw new InsertException(Auction.class.getSimpleName(), errorMsg);
        } catch (InsertException e) {
            throw new RuntimeException(e);
        }
    }


    private void validateMaxBidsAmount(Integer accountId, Integer auctionId, List<AutoBidRQ> autoBidsRQ) {
        autoBidsRQ.forEach(autoBidRQ -> {
            List<Bid> previousBidsPerAuto = this.bidRepository.findByAccountIdAndAuctionIdAndAutoId(accountId, auctionId, autoBidRQ.getAutoId());
            handleMaxBidAmount(previousBidsPerAuto, autoBidRQ.getAmount());
        });
    }

    private void handleMaxBidAmount(List<Bid> previousBids, Float currentBidAmount) {
        if (previousBids != null && !previousBids.isEmpty()) {
            Float maxAmount = previousBids.stream()
                    .max(Comparator.comparing(Bid::getAmount))
                    .map(Bid::getAmount)
                    .orElse(0F);

            if (maxAmount >= currentBidAmount) {
                log.error("Error, the bid amount cannot be equal to or less than the maximum bid already placed.");
                errorMsg = "Error, the bid amount cannot be equal to or less than the maximum bid already placed.";
                try {
                    throw new InsertException(Bid.class.getSimpleName(), errorMsg);
                } catch (InsertException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void validateBidAmount(List<AutoBidRQ> autoBidsRQ) {
        autoBidsRQ.forEach(autoBidRQ -> {
            if (autoBidRQ.getAmount() <= 0) {
                log.error("Error, the bid amount cannot be equal to or less than zero.");
                errorMsg = "Error, the bid amount cannot be equal to or less than zero.";
                try {
                    throw new InsertException(Bid.class.getSimpleName(), errorMsg);
                } catch (InsertException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Bid getHighestBid(List<Bid> bids) {
        Optional<Bid> highestBid = bids.stream()
                .max(Comparator.comparing(Bid::getAmount));
        return highestBid.orElse(null);
    }
}
