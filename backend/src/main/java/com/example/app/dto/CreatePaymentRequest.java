package com.example.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
  @NotNull @DecimalMin(value = "0.01", inclusive = true) private BigDecimal amount;
  private String payerName;
  private Long participantId;
  private String note;
}

