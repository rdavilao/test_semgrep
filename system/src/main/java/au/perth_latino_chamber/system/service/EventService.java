package au.perth_latino_chamber.system.service;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Event;
import au.perth_latino_chamber.system.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    public final EventRepository eventRepository;

    private String errorMsg;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getCurrentEvents() throws DocumentNotFoundException {
        try {
            log.info("Getting current events information");

            LocalDate localDate = LocalDate.now();
            Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Event> events = this.eventRepository.findByDateAfterAndEnabled(currentDate, true);

            if (events.isEmpty()) {
                this.errorMsg = "There is no information from any current event available.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }

            return events;
        } catch (Exception ex) {
            log.error("Error, getting current events information: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting current events information.");
        }
    }

    public List<Event> getPastEvents() throws DocumentNotFoundException {
        try {
            log.info("Getting past events information");

            LocalDate localDate = LocalDate.now();
            Date currentDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Event> events = this.eventRepository.findByDateBeforeAndEnabled(currentDate, true);

            if (events.isEmpty()) {
                this.errorMsg = "There is no information from any past event available.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }

            return events;
        } catch (Exception ex) {
            log.error("Error, getting past events information: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting past events information.");
        }
    }

    public Event getEventById(int id) throws DocumentNotFoundException {
        try {
            log.info("Getting event information by id: " + id);
            Optional<Event> optionalEvent = this.eventRepository.findById(id);
            if (!optionalEvent.isPresent()) {
                this.errorMsg = "Event id: " + id + " doesn't exists.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }
            assert optionalEvent.isPresent();
            return optionalEvent.get();
        } catch (Exception ex) {
            log.error("Error, getting event information by id: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting event information by id.");
        }

    }
}
