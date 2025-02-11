package au.com.projects.tender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "auto")
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fk_auction_id", referencedColumnName = "auction_id")
    private Auction auction;

    @Column(name = "auto_make", nullable = false)
    private String make;

    @Column(name = "auto_model", nullable = false)
    private String model;

    @Column(name = "auto_plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "auto_image_to_display")
    private String imgToDisplay;

    @Column(name = "auto_rego", nullable = false)
    private Date rego;

    @Column(name = "auto_features", nullable = false)
    private String features;

    @Column(name = "auto_transmission", nullable = false)
    private String transmission;

    @Column(name = "auto_kms", nullable = false)
    private Float kms;

    @Column(name = "auto_color", nullable = false)
    private String color;

    @Column(name = "auto_images")
    private String images;
}