package io.dawn.ivrauto.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "responses")
public class Response {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "response")
  private String response;

  @Column(name = "session_sid")
  private String sessionSid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private Question question;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "candidate_id")
  private Candidate candidate;

  @Column(name = "date")
  private LocalDate date;

  public Response() {}

  public Response(
      String response, String sessionSid, Question question, Candidate candidate, LocalDate date) {
    this.response = response;
    this.sessionSid = sessionSid;
    this.question = question;
    this.candidate = candidate;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public String getSessionSid() {
    return sessionSid;
  }

  public void setSessionSid(String sessionSid) {
    this.sessionSid = sessionSid;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public Candidate getCandidate() {
    return candidate;
  }

  public void setCandidate(Candidate candidate) {
    this.candidate = candidate;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public boolean isRecording() {
    return this.getResponse().contains("/Recordings/") && this.getResponse().startsWith("https");
  }
}
