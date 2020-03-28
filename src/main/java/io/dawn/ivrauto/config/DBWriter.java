package io.dawn.ivrauto.config;

import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.repository.CandidateRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DBWriter implements ItemWriter<Candidate> {

  @Autowired private CandidateRepository candidateRepository;

  @Override
  public void write(List<? extends Candidate> candidates) throws Exception {
    log.info("Candidates CSV load complete..");
    candidateRepository.saveAll(candidates);
  }
}
