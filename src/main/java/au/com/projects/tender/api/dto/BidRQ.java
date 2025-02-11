package au.com.projects.tender.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BidRQ {

    private String email;
    private Integer auctionId;
    private List<AutoBidRQ> autoBidRQList;
}
