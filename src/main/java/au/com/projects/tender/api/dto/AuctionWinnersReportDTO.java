package au.com.projects.tender.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AuctionWinnersReportDTO {
    private Integer autoId;
    private String emailWinner;
    private String phoneWinner;
    private Float bidAmount;
    private String make;
    private String model;
    private String plateNumber;
    private String imgToDisplay;
    private Date rego;
    private String features;
    private String transmission;
    private Float kms;
    private String color;
    private String images;
}
