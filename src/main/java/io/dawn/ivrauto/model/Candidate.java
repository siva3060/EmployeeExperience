package io.dawn.ivrauto.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "candidate")
public class Candidate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "mobile_number")
  private String mobileNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "skills")
  private String skills;

  @Column(name = "years_of_exp")
  private String yearsOfExp;

  @Column(name = "spoc")
  private String spoc;

  @Column(name = "delivery_lead")
  private String deliveryLead;

  @Column(name = "interview_status")
  private String interviewStatus;

  @Column(name = "doj")
  private LocalDate doj;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSkills() {
    return skills;
  }

  public void setSkills(String skills) {
    this.skills = skills;
  }

  public String getYearsOfExp() {
    return yearsOfExp;
  }

  public void setYearsOfExp(String yearsOfExp) {
    this.yearsOfExp = yearsOfExp;
  }

  public String getSpoc() {
    return spoc;
  }

  public void setSpoc(String spoc) {
    this.spoc = spoc;
  }

  public String getDeliveryLead() {
    return deliveryLead;
  }

  public void setDeliveryLead(String deliveryLead) {
    this.deliveryLead = deliveryLead;
  }

  public String getInterviewStatus() {
    return interviewStatus;
  }

  public void setInterviewStatus(String interviewStatus) {
    this.interviewStatus = interviewStatus;
  }

  public LocalDate getDoj() {
    return doj;
  }

  public void setDoj(LocalDate doj) {
    this.doj = doj;
  }
}
