package io.dawn.ivrauto.service;

import io.dawn.ivrauto.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {
  private CandidateRepository candidateRepository;

  @Autowired
  public CandidateService(CandidateRepository candidateRepository) {
    this.candidateRepository = candidateRepository;
  }

  public String findACandidateToCall(String callee) {
    return candidateRepository.findCandidateNumberByName(callee);
  }
}
