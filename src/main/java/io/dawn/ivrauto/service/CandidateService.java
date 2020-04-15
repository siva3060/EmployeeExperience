package io.dawn.ivrauto.service;

import io.dawn.ivrauto.model.Candidate;
import io.dawn.ivrauto.repository.CandidateRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {
  private CandidateRepository candidateRepository;

  @Autowired
  public CandidateService(CandidateRepository candidateRepository) {
    this.candidateRepository = candidateRepository;
  }

  public List<Candidate> findCandidate() {
    return candidateRepository.findCandidate();
  }

  public Optional<Candidate> findCandidateById(long cid) {
    return candidateRepository.findById(cid);
  }
}
