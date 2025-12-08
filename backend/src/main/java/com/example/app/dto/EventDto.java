package com.example.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
  private Long id;
  @NotBlank private String name;
  private String description;
  @NotNull @DecimalMin(value = "0.0", inclusive = true) private BigDecimal adultBudget;
  @NotNull @DecimalMin(value = "0.0", inclusive = true) private BigDecimal childBudget;
  @DecimalMin(value = "0.0", inclusive = true) private BigDecimal generalCosts;
  private Long creatorId;
  private String creatorUsername;
  private LocalDateTime createdAt;
  @Builder.Default private List<ParticipantDto> participants = new ArrayList<>();
  @Builder.Default private List<PaymentDto> payments = new ArrayList<>();
  private BigDecimal totalBudget;
  private BigDecimal budgetPerPayer;
  private Integer numberOfPayers;
}

