package au.perth_latino_chamber.system.repository;

import au.perth_latino_chamber.system.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Integer> {

    Optional<Event> findById(Integer id);

    List<Event> findByDateAfterAndEnabled(Date currentDate, Boolean enabled);

    List<Event> findByDateBeforeAndEnabled(Date currentDate, Boolean enabled);
}
