package au.com.projects.tender.repository;

import au.com.projects.tender.model.Auction;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface AuctionRepository extends CrudRepository<Auction, Integer> {

    List<Auction> findByEnabled(boolean enabled);

    List<Auction> findByStartDateBeforeAndEndDateAfter(Date currentDate, Date currentDate1);

    List<Auction> findByStartDateAfter(Date currentDate);

    Auction findByNumber(Integer number);
}
