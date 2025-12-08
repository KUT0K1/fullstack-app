package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  @Column(length = 1000)
  private String description;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal adultBudget;

  @NotNull
  @DecimalMin(value = "0.0", inclusive = true)
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal childBudget;

  @DecimalMin(value = "0.0", inclusive = true)
  @Column(precision = 10, scale = 2)
  @Builder.Default
  private BigDecimal generalCosts = BigDecimal.ZERO;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private User creator;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Participant> participants = new ArrayList<>();

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Payment> payments = new ArrayList<>();

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}

