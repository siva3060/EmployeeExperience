package io.dawn.ivrauto.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "screenings")
public class Screening {
  @OneToMany(mappedBy = "screening")
  @OrderBy("id ASC")
  List<Question> questions;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "date")
  private Date date;

  public Screening() {}

  public Screening(String title, Date date) {
    this.title = title;
    this.date = date;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(List<Question> questions) {
    this.questions = questions;
  }

  public Question getQuestionByNumber(int number) {
    return isValidQuestionNumber(number) ? questions.get(number - 1) : null;
  }

  public boolean isValidQuestionNumber(int number) {
    return number > 0 && number <= questions.size();
  }

  public boolean isLastQuestion(Question currentQuestion) {
    int nextNumber = getNextQuestionNumber(currentQuestion);
    return !isValidQuestionNumber(nextNumber);
  }

  public int getNextQuestionNumber(Question currentQuestion) {
    return questions.indexOf(currentQuestion) + 2;
  }
}
