package au.perth_latino_chamber.system.repository;

import au.perth_latino_chamber.system.model.Job;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends CrudRepository<Job, Integer> {

    Optional<Job> findById(Integer id);

    List<Job> findByStartDateBeforeAndClosingDateAfterAndEnabled(LocalDate startDate, LocalDate closingDate, Boolean enabled);
}
