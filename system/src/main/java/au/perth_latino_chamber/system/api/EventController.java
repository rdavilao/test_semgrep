package au.perth_latino_chamber.system.api;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Event;
import au.perth_latino_chamber.system.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/perth-latino-chamber/event")
@Slf4j
public class EventController {

    public final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/getCurrentEvents")
    public ResponseEntity<List<Event>> getCurrentEvents() {
        try {
            return ResponseEntity.ok(this.eventService.getCurrentEvents());
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getPastEvents")
    public ResponseEntity<List<Event>> getPastEvents() {
        try {
            return ResponseEntity.ok(this.eventService.getPastEvents());
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getEventInfo")
    public ResponseEntity<Event> getEventInfo(@RequestParam("eventId") Integer eventId) {
        try {
            return ResponseEntity.ok(this.eventService.getEventById(eventId));
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}
