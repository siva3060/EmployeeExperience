package io.dawn.ivrauto.repository;

import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Response;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
  String GETDATE = "SELECT RESPONSE FROM RESPONSES WHERE QUESTION_ID IN (2, 3)";

  Response getBySessionSidAndQuestion(String sessionSid, Question question);

  @Query(value = GETDATE, nativeQuery = true)
  List<String> getDate();
}
