package io.dawn.ivrauto.repository;

import io.dawn.ivrauto.model.Response;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

  @Query(
      value =
          "select r.id, r.candidate_id, r.date, r.question_id, r.response, r.session_sid\n"
              + "from responses r\n"
              + "where r.session_sid = :session_sid \n"
              + "and r.question_id = :qid",
      nativeQuery = true)
  Response findProperResponse(@Param("session_sid") String session_sid, @Param("qid") Long qid);

  @Query(
      value = "SELECT response FROM RESPONSES WHERE QUESTION_ID IN (1, 2) AND candidate_id = :cid",
      nativeQuery = true)
  List<String> generateExamDate(long cid);
}
