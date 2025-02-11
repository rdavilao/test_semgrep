package au.perth_latino_chamber.system.service;

import au.perth_latino_chamber.system.exception.DocumentNotFoundException;
import au.perth_latino_chamber.system.model.Job;
import au.perth_latino_chamber.system.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobService {

    private final JobRepository jobRepository;

    private String errorMsg;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllAvailableJobs() throws DocumentNotFoundException {
        try {
            log.info("Getting jobs information");
            LocalDate currentDate = LocalDate.now();
            List<Job> jobs = this.jobRepository.findByStartDateBeforeAndClosingDateAfterAndEnabled(currentDate, currentDate, true);

            if (jobs.isEmpty()) {
                this.errorMsg = "There is no information from any available jobs.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }

            return jobs;
        } catch (Exception ex) {
            log.error("Error, getting all jobs information: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting all jobs information.");
        }
    }

    public Job getJobInformationById(int id) throws DocumentNotFoundException {
        try {
            log.info("Getting business information by id: " + id);
            Optional<Job> optionalJob = this.jobRepository.findById(id);
            if (!optionalJob.isPresent()) {
                this.errorMsg = "Job id: " + id + " doesn't exists.";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }
            assert optionalJob.isPresent();
            return optionalJob.get();
        } catch (Exception ex) {
            log.error("Error, getting job information by id: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting job information by id.");
        }

    }

    public List<String> getAllJobsClassifications() throws DocumentNotFoundException {
        try {
            log.info("Getting all jobs classifications.");
            ArrayList<Job> allJobs = (ArrayList<Job>) this.jobRepository.findAll();
            List<String> jobsClassifications = allJobs.stream()
                    .map(Job::getClassification)
                    .distinct()
                    .collect(Collectors.toList());

            if (jobsClassifications.isEmpty()) {
                this.errorMsg = "No classification exists";
                log.warn(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }

            Collections.sort(jobsClassifications);

            return jobsClassifications;

        } catch (Exception ex) {
            log.error("Error, getting jobs classifications: " + ex);
            throw new DocumentNotFoundException(errorMsg != null ? errorMsg : "Error, getting jobs classifications.");
        }
    }
}
