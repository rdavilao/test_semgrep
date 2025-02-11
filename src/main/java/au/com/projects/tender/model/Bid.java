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
@Table(name = "bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id", nullable = false)
    private Integer id;

    @Column(name = "bid_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date bidDate;

    @ManyToOne
    @JoinColumn(name = "fk_account_id", referencedColumnName = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "fk_auction_id", referencedColumnName = "auction_id")
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "fk_auto_id", referencedColumnName = "auto_id")
    private Auto auto;

    @Column(name = "bid_amount", nullable = false)
    private Float amount;
}