package io.dawn.ivrauto.service;

import io.dawn.ivrauto.model.Question;
import io.dawn.ivrauto.model.Screening;
import io.dawn.ivrauto.repository.ScreeningRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class ScreeningService {
  private ScreeningRepository screeningRepository;

  @Autowired
  public ScreeningService(ScreeningRepository screeningRepository) {
    this.screeningRepository = screeningRepository;
  }

  public Screening create(Screening screening) {
    screeningRepository.save(screening);
    return screening;
  }

  public void delete(Long id) {
    screeningRepository.deleteById(id);
  }

  public void deleteAll() {
    screeningRepository.deleteAll();
  }

  public Long count() {
    return screeningRepository.count();
  }

  public List<Screening> findAll() {
    return screeningRepository.findAll();
  }

  public Optional<Screening> find(Long id) {
    return screeningRepository.findById(id);
  }

  public Screening findLast() {
    List<Screening> screenings = screeningRepository.findAll(Sort.by(Direction.DESC, "id"));
    return screenings.isEmpty() ? null : screenings.get(0);
  }

  public List<Question> findScreeningQuestions(Screening screening) {
    return screening.getQuestions();
  }
}
