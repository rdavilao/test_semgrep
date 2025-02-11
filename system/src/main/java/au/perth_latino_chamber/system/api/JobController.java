package au.perth_latino_chamber.system.api;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Job;
import au.perth_latino_chamber.system.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/perth-latino-chamber/job")
@Slf4j
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/getCurrentJobs")
    public ResponseEntity<List<Job>> getCurrentJobs() {
        try {
            return ResponseEntity.ok(this.jobService.getAllAvailableJobs());
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getJobInfo")
    public ResponseEntity<Job> getJobInfo(@RequestParam("jobId") Integer jobId) {
        try {
            return ResponseEntity.ok(this.jobService.getJobInformationById(jobId));
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getJobsClassification")
    public ResponseEntity<List<String>> getJobsClassification() {
        try {
            return ResponseEntity.ok(this.jobService.getAllJobsClassifications());
        } catch (DocumentNotFoundException ex) {
            log.error("Error: " + ex);
            return ResponseEntity.notFound().build();
        }
    }
}

