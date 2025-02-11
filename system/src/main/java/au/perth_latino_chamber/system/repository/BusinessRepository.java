package au.perth_latino_chamber.system.repository;

import au.perth_latino_chamber.system.model.Business;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface BusinessRepository extends CrudRepository<Business, Integer> {

    Optional<Business> findById(Integer businessId);

    List<Business> findByEnabled(Boolean enabled);
}
