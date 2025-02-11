package au.perth_latino_chamber.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event_chamber")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false)
    private Integer id;

    @Column(name = "event_title", nullable = false)
    private String title;

    @Column(name = "event_location")
    private String address;

    @Column(name = "event_google_maps_link")
    private String googleMapsLink;

    @Column(name = "event_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "event_phone")
    private String phone;

    @Column(name = "event_website_link")
    private String websiteLink;

    @Column(name = "event_register_link")
    private String registerLink;

        @Column(name = "event_img")
        private String img;

    @Column(name = "event_description", nullable = false)
    private String description;

    @Column(name = "event_enabled", nullable = false)
    private Boolean enabled;
}
