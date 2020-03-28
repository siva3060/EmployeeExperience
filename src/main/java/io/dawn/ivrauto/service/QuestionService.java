package io.dawn.ivrauto.service;

import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.QuestionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {
  private QuestionRepository questionRepository;

  @Autowired
  public QuestionService(QuestionRepository questionRepository) {
    this.questionRepository = questionRepository;
  }

  public Question save(Question question) {
    questionRepository.save(question);
    return question;
  }

  public void delete(Long id) {
    questionRepository.deleteById(id);
  }

  public void deleteAll() {
    questionRepository.deleteAll();
  }

  public Long count() {
    return questionRepository.count();
  }

  public List<Question> findAll() {
    return questionRepository.findAll();
  }

  public Optional<Question> find(Long id) {
    return questionRepository.findById(id);
  }

  public Question findFirst(Screening screening) throws IndexOutOfBoundsException {
    return questionRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).get(0);
  }
}
