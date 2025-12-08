package com.example.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
  private Long id;
  @NotNull @DecimalMin(value = "0.01", inclusive = true) private BigDecimal amount;
  @NotBlank private String payerName;
  private String note;
  private Long eventId;
  private LocalDateTime createdAt;
}

