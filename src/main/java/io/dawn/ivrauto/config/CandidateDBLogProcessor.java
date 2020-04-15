package io.dawn.ivrauto.config;

import io.dawn.ivrauto.model.Candidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CandidateDBLogProcessor implements ItemProcessor<Candidate, Candidate> {

  @Override
  public Candidate process(Candidate candidate) throws Exception {
    return candidate;
  }
}
