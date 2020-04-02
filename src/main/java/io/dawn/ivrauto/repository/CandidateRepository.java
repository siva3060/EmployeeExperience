package io.dawn.ivrauto.repository;

import io.dawn.ivrauto.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

  @Async
  @Query(
      value = "SELECT c.candidate_mobile_number FROM candidates c where c.candidate_name = :callee",
      nativeQuery = true)
  String findCandidateNumberByName(@Param("callee") String callee);
}
