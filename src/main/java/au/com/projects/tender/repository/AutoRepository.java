package au.com.projects.tender.repository;

import au.com.projects.tender.model.Auto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AutoRepository extends CrudRepository<Auto, Integer> {

    List<Auto> findByAuctionId(Integer auctionId);
}
