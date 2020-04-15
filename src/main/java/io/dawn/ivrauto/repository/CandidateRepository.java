package io.dawn.ivrauto.repository;

import io.dawn.ivrauto.model.Candidate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

  @Query(
      value = "SELECT c.skills FROM candidate c WHERE c.mobile_number = :number",
      nativeQuery = true)
  String findSkillByCandidateNumber(@Param("number") String number);

  @Query(
      value =
          "SELECT c.id, c.name, c.mobile_number, c.email, c.skills, c.years_of_exp, "
              + "c.spoc, c.delivery_lead, c.interview_status, c.doj FROM candidate c"
              + " WHERE interview_status IS NULL",
      nativeQuery = true)
  List<Candidate> findCandidate();

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(
      value =
          "UPDATE candidate SET interview_status = 'COMPLETED' WHERE id = :cid",
      nativeQuery = true)
  void updateInterviewStatus(@Param("cid") Long cid);
}
