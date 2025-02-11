package au.com.projects.tender.api.dto;

import au.com.projects.tender.model.Auto;
import au.com.projects.tender.model.Bid;
import lombok.Data;

import java.util.Date;

@Data
public class BidInformationCompleteDTO {

    private Integer autoId;
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
    private Float bidAmount;

}
