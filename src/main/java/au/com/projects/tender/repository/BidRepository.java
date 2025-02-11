package au.com.projects.tender.repository;

import au.com.projects.tender.model.Bid;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BidRepository extends CrudRepository<Bid, Integer> {

    List<Bid> findByAccountIdAndAuctionIdAndAutoId(Integer accountId, Integer auctionId, Integer autoId);

    List<Bid> findByAuctionIdAndAutoId(Integer auctionId, Integer autoId);

    List<Bid> findByAccountId(Integer accountId);

    List<Bid> findByAccountIdAndAuctionId(Integer accountId, Integer auctionId);

    List<Bid> findByAuctionId(Integer auctionId);

    List<Bid> findByAutoId(Integer autoId);


}
