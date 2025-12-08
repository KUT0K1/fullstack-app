package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ParticipantType type;

  @DecimalMin(value = "0.0", inclusive = true)
  @Column(precision = 10, scale = 2)
  private BigDecimal customBudget;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isCouple = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partner_id")
  private Participant partner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;

  public enum ParticipantType {
    ADULT,
    CHILD
  }
}

