package au.com.projects.tender.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id", nullable = false)
    private Integer id;

    @Column(name = "banner_img_name", nullable = false)
    private String imgName;

    @Column(name = "banner_is_mobile", nullable = false)
    private Boolean isMobile;

    @Column(name = "banner_img_order", nullable = false)
    private Integer imgOrder;
}
