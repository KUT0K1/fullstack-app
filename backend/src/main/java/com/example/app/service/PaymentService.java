package com.example.app.service;

import com.example.app.dto.CreatePaymentRequest;
import com.example.app.dto.PaymentDto;
import com.example.app.entity.Event;
import com.example.app.entity.Participant;
import com.example.app.entity.Payment;
import com.example.app.repository.EventRepository;
import com.example.app.repository.ParticipantRepository;
import com.example.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final EventRepository eventRepository;
  private final ParticipantRepository participantRepository;

  @Transactional
  public PaymentDto createPayment(Long eventId, CreatePaymentRequest request, Long userId) {
    Event event =
        eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

    // Prüfe ob User der Ersteller ist
    if (!event.getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to add payments to this event");
    }

    Payment payment = new Payment();
    payment.setAmount(request.getAmount());
    payment.setPayerName(request.getPayerName());
    payment.setNote(request.getNote());
    payment.setEvent(event);

    // Participant zuordnen falls vorhanden
    if (request.getParticipantId() != null) {
      Participant participant =
          participantRepository
              .findById(request.getParticipantId())
              .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
      
      // Prüfe ob Participant zum Event gehört
      if (!participant.getEvent().getId().equals(eventId)) {
        throw new IllegalArgumentException("Participant does not belong to this event");
      }
      
      payment.setParticipant(participant);
      // Wenn kein payerName gesetzt ist, verwende den Participant-Namen
      if (payment.getPayerName() == null || payment.getPayerName().isEmpty()) {
        payment.setPayerName(participant.getName());
      }
    }

    payment = paymentRepository.save(payment);
    return mapToDto(payment);
  }

  @Transactional
  public PaymentDto updatePayment(Long id, CreatePaymentRequest request, Long userId) {
    Payment payment =
        paymentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));

    // Prüfe ob User der Event-Ersteller ist
    if (!payment.getEvent().getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to update this payment");
    }

    payment.setAmount(request.getAmount());
    payment.setPayerName(request.getPayerName());
    payment.setNote(request.getNote());

    // Participant zuordnen falls vorhanden
    if (request.getParticipantId() != null) {
      Participant participant =
          participantRepository
              .findById(request.getParticipantId())
              .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
      
      // Prüfe ob Participant zum Event gehört
      if (!participant.getEvent().getId().equals(payment.getEvent().getId())) {
        throw new IllegalArgumentException("Participant does not belong to this event");
      }
      
      payment.setParticipant(participant);
      // Wenn kein payerName gesetzt ist, verwende den Participant-Namen
      if (payment.getPayerName() == null || payment.getPayerName().isEmpty()) {
        payment.setPayerName(participant.getName());
      }
    } else {
      // Participant-Zuordnung entfernen
      payment.setParticipant(null);
    }

    payment = paymentRepository.save(payment);
    return mapToDto(payment);
  }

  @Transactional
  public void deletePayment(Long id, Long userId) {
    Payment payment =
        paymentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found"));

    // Prüfe ob User der Event-Ersteller ist
    if (!payment.getEvent().getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to delete this payment");
    }

    paymentRepository.delete(payment);
  }

  private PaymentDto mapToDto(Payment payment) {
    PaymentDto.PaymentDtoBuilder builder =
        PaymentDto.builder()
            .id(payment.getId())
            .amount(payment.getAmount())
            .payerName(payment.getPayerName())
            .note(payment.getNote())
            .eventId(payment.getEvent().getId())
            .createdAt(payment.getCreatedAt());

    // Participant-Informationen hinzufügen
    if (payment.getParticipant() != null) {
      Participant participant = payment.getParticipant();
      builder.participantId(participant.getId())
          .participantName(participant.getName());

      // Partner-Informationen hinzufügen falls vorhanden
      if (participant.getPartner() != null) {
        builder.partnerId(participant.getPartner().getId())
            .partnerName(participant.getPartner().getName());
      }
    }

    return builder.build();
  }
}

