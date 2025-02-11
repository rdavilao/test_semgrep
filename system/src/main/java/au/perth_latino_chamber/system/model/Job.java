package au.perth_latino_chamber.system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "job_fk_business_id", referencedColumnName = "business_id")
    private Business business;

    @Column(name = "job_title", nullable = false)
    private String title;

    @Column(name = "job_email")
    private String email;

    @Column(name = "job_phone")
    private String phone;

    @Column(name = "job_remote", nullable = false)
    private boolean remote;

    @Column(name = "job_salary")
    private String salary;

    @Column(name = "job_application_link")
    private String applicationLink;

    @Column(name = "job_start_date")
    private LocalDate startDate;

    @Column(name = "job_closing_date")
    private LocalDate closingDate;

    @Column(name = "job_description", nullable = false)
    private String description;

    @Column(name = "job_type", nullable = false)
    private String type;

    @Column(name = "job_classification", nullable = false)
    private String classification;

    @Column(name = "job_requirements", nullable = false)
    private String requirements;

    @Column(name = "job_responsibilities", nullable = false)
    private String responsabilities;

    @Column(name = "job_benefits", nullable = false)
    private String benefits;

    @Column(name = "job_enabled", nullable = false)
    private Boolean enabled;
}
