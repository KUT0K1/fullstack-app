package com.example.app.dto;

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
public class CreateEventRequest {
  @NotBlank private String name;
  private String description;
  @NotNull @DecimalMin(value = "0.0", inclusive = true) private BigDecimal adultBudget;
  @NotNull @DecimalMin(value = "0.0", inclusive = true) private BigDecimal childBudget;
  @DecimalMin(value = "0.0", inclusive = true) private BigDecimal generalCosts;
}

