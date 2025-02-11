package au.perth_latino_chamber.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "business")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id", nullable = false)
    private Integer id;

    @Column(name = "business_title", nullable = false)
    private String title;

    @Column(name = "business_address")
    private String address;

    @Column(name = "business_google_maps_link")
    private String googleMapsLink;

    @Column(name = "business_phone", nullable = false)
    private String phone;

    @Column(name = "business_image")
    private String img;

    @Column(name = "business_website_link")
    private String websiteLink;

    @Column(name = "business_description", nullable = false)
    private String description;

    @Column(name = "business_enabled", nullable = false)
    private Boolean enabled;
}
