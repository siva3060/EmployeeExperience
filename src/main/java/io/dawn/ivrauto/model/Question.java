package io.dawn.ivrauto.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "question")
public class Question {
  @OneToMany(mappedBy = "question")
  @OrderBy("id ASC")
  List<Response> responses;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "body")
  private String body;

  @Column(name = "type")
  private String type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "screening_id")
  private Screening screening;

  @Column(name = "date")
  private LocalDate date;

  public Question() {}

  public Question(String body, String type, Screening screening, LocalDate date) {
    this.body = body;
    this.type = type;
    this.screening = screening;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Screening getScreening() {
    return screening;
  }

  public void setScreening(Screening screening) {
    this.screening = screening;
  }

  public List<Response> getResponses() {
    return responses;
  }

  public void setResponses(List<Response> responses) {
    this.responses = responses;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
