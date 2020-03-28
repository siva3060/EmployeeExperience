package io.dawn.ivrauto.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "candidates")
public class Candidate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "candidate_name")
  private String candidateName;

  @Column(name = "candidate_mobile_number")
  private String candidateMobileNumber;

  @Column(name = "candidate_email")
  private String candidateEmail;

  @Column(name = "candidate_skills")
  private String candidateSkills;

  @Column(name = "spoc")
  private String spoc;

  @Column(name = "delivery_lead")
  private String deliveryLead;
}
