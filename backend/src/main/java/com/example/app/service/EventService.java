package com.example.app.service;

import com.example.app.dto.*;
import com.example.app.entity.Event;
import com.example.app.entity.Participant;
import com.example.app.entity.User;
import com.example.app.repository.EventRepository;
import com.example.app.repository.ParticipantRepository;
import com.example.app.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {

  private final EventRepository eventRepository;
  private final ParticipantRepository participantRepository;
  private final UserRepository userRepository;
  private final BudgetCalculationService budgetCalculationService;

  @Transactional
  public EventDto createEvent(CreateEventRequest request, Long creatorId) {
    User creator =
        userRepository
            .findById(creatorId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Event event =
        Event.builder()
            .name(request.getName())
            .description(request.getDescription())
            .adultBudget(request.getAdultBudget())
            .childBudget(request.getChildBudget())
            .generalCosts(
                request.getGeneralCosts() != null ? request.getGeneralCosts() : java.math.BigDecimal.ZERO)
            .creator(creator)
            .build();

    event = eventRepository.save(event);
    return mapToDto(event);
  }

  @Transactional(readOnly = true)
  public EventDto getEventById(Long id, Long userId) {
    Event event =
        eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));

    // Prüfe ob User der Ersteller ist
    if (!event.getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to view this event");
    }

    return mapToDto(event);
  }

  @Transactional(readOnly = true)
  public List<EventDto> getEventsByCreator(Long creatorId) {
    List<Event> events = eventRepository.findByCreatorId(creatorId);
    return events.stream().map(this::mapToDto).collect(Collectors.toList());
  }

  @Transactional
  public EventDto updateEvent(Long id, CreateEventRequest request, Long userId) {
    Event event =
        eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));

    if (!event.getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to update this event");
    }

    event.setName(request.getName());
    event.setDescription(request.getDescription());
    event.setAdultBudget(request.getAdultBudget());
    event.setChildBudget(request.getChildBudget());
    event.setGeneralCosts(
        request.getGeneralCosts() != null ? request.getGeneralCosts() : java.math.BigDecimal.ZERO);

    event = eventRepository.save(event);
    return mapToDto(event);
  }

  @Transactional
  public void deleteEvent(Long id, Long userId) {
    Event event =
        eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event not found"));

    if (!event.getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to delete this event");
    }

    eventRepository.delete(event);
  }

  private EventDto mapToDto(Event event) {
    List<Participant> participants = participantRepository.findByEventId(event.getId());
    List<Participant> payers =
        participants.stream()
            .filter(p -> p.getType() == com.example.app.entity.Participant.ParticipantType.ADULT)
            .collect(Collectors.toList());

    java.math.BigDecimal totalBudget = budgetCalculationService.calculateTotalBudget(event, participants);
    java.math.BigDecimal budgetPerPayer =
        budgetCalculationService.calculateBudgetPerPayer(event, participants, payers);
    int numberOfPayers = budgetCalculationService.calculateNumberOfPayers(payers);

    return EventDto.builder()
        .id(event.getId())
        .name(event.getName())
        .description(event.getDescription())
        .adultBudget(event.getAdultBudget())
        .childBudget(event.getChildBudget())
        .generalCosts(event.getGeneralCosts())
        .creatorId(event.getCreator().getId())
        .creatorUsername(event.getCreator().getUsername())
        .createdAt(event.getCreatedAt())
        .participants(
            participants.stream()
                .map(
                    p -> {
                      ParticipantDto dto = mapParticipantToDto(p);
                      dto.setCalculatedBudget(
                          budgetCalculationService.calculateParticipantBudget(p, event));
                      return dto;
                    })
                .collect(Collectors.toList()))
        .payments(
            event.getPayments().stream().map(this::mapPaymentToDto).collect(Collectors.toList()))
        .totalBudget(totalBudget)
        .budgetPerPayer(budgetPerPayer)
        .numberOfPayers(numberOfPayers)
        .build();
  }

  private ParticipantDto mapParticipantToDto(Participant participant) {
    return ParticipantDto.builder()
        .id(participant.getId())
        .name(participant.getName())
        .type(participant.getType())
        .customBudget(participant.getCustomBudget())
        .isCouple(participant.getIsCouple())
        .partnerId(participant.getPartner() != null ? participant.getPartner().getId() : null)
        .userId(participant.getUser() != null ? participant.getUser().getId() : null)
        .eventId(participant.getEvent().getId())
        .build();
  }

  private PaymentDto mapPaymentToDto(com.example.app.entity.Payment payment) {
    return PaymentDto.builder()
        .id(payment.getId())
        .amount(payment.getAmount())
        .payerName(payment.getPayerName())
        .note(payment.getNote())
        .eventId(payment.getEvent().getId())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}

