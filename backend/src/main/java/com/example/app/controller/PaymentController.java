package com.example.app.controller;

import com.example.app.dto.CreatePaymentRequest;
import com.example.app.dto.PaymentDto;
import com.example.app.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

  private final com.example.app.service.PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentDto> createPayment(
      @PathVariable Long eventId,
      @Valid @RequestBody CreatePaymentRequest request,
      @CurrentUser Long userId) {
    try {
      PaymentDto payment = paymentService.createPayment(eventId, request, userId);
      return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentDto> updatePayment(
      @PathVariable Long id,
      @Valid @RequestBody CreatePaymentRequest request,
      @CurrentUser Long userId) {
    try {
      PaymentDto payment = paymentService.updatePayment(id, request, userId);
      return ResponseEntity.ok(payment);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(@PathVariable Long id, @CurrentUser Long userId) {
    try {
      paymentService.deletePayment(id, userId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}

