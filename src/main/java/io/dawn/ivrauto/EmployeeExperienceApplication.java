package io.dawn.ivrauto;

import io.dawn.ivrauto.repository.QuestionRepository;
import io.dawn.ivrauto.repository.ScreeningRepository;
import io.dawn.ivrauto.service.QuestionService;
import io.dawn.ivrauto.service.ScreeningService;
import io.dawn.ivrauto.util.ScreeningParser;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
@Slf4j
public class EmployeeExperienceApplication implements CommandLineRunner {
  private final QuestionRepository questionRepository;
  private final ScreeningRepository screeningRepository;

  @Autowired JobLauncher jobLauncher;
  @Autowired Job job;

  @Autowired
  public EmployeeExperienceApplication(
      QuestionRepository questionRepository, ScreeningRepository screeningRepository) {
    this.questionRepository = questionRepository;
    this.screeningRepository = screeningRepository;
  }

  public static void main(String[] args) {
    SpringApplication.run(EmployeeExperienceApplication.class, args);
  }

  @PreDestroy
  public void loadCSV()
      throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
          JobRestartException, JobInstanceAlreadyCompleteException {
    Map<String, JobParameter> maps = new HashMap<>();
    maps.put("time", new JobParameter(System.currentTimeMillis()));
    JobParameters parameters = new JobParameters(maps);
    JobExecution jobExecution = jobLauncher.run(job, parameters);

    log.info("Job Execution: " + jobExecution.getStatus());
    log.info("Batch is Running...");
    while (jobExecution.isRunning()) {
      log.info("...");
    }
    log.info("Job Execution: " + jobExecution.getStatus());
  }

  /**
   * Method that runs on app initialization. It will parse and insert the questions in the DB on
   * every app initialization
   */
  @Override
  public void run(String... strings) throws Exception {
    ScreeningService screeningService = new ScreeningService(screeningRepository);
    QuestionService questionService = new QuestionService(questionRepository);

    ScreeningParser screeningParser = new ScreeningParser(screeningService, questionService);
    screeningParser.parse("screening.json");
  }
}
