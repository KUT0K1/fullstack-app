package com.example.app.dto;

import com.example.app.entity.Participant.ParticipantType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipantRequest {
  @NotBlank private String name;
  @NotNull private ParticipantType type;
  @DecimalMin(value = "0.0", inclusive = true) private BigDecimal customBudget;
  private Boolean isCouple;
  private Long partnerId;
  private Long userId;
}

