package com.example.app.service;

import com.example.app.dto.CreateParticipantRequest;
import com.example.app.dto.ParticipantDto;
import com.example.app.entity.Event;
import com.example.app.entity.Participant;
import com.example.app.entity.User;
import com.example.app.repository.EventRepository;
import com.example.app.repository.ParticipantRepository;
import com.example.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

  private final ParticipantRepository participantRepository;
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final BudgetCalculationService budgetCalculationService;

  @Transactional
  public ParticipantDto createParticipant(Long eventId, CreateParticipantRequest request, Long userId) {
    Event event =
        eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

    // Prüfe ob User der Ersteller ist
    if (!event.getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to add participants to this event");
    }

    Participant participant = new Participant();
    participant.setName(request.getName());
    participant.setType(request.getType());
    participant.setCustomBudget(request.getCustomBudget());
    participant.setIsCouple(request.getIsCouple() != null ? request.getIsCouple() : false);
    participant.setEvent(event);

    // Partner verknüpfen falls vorhanden
    if (request.getPartnerId() != null) {
      Participant partner =
          participantRepository
              .findById(request.getPartnerId())
              .orElseThrow(() -> new IllegalArgumentException("Partner not found"));
      participant.setPartner(partner);
      // Partner auch als Paar markieren
      partner.setIsCouple(true);
      partner.setPartner(participant);
      participantRepository.save(partner);
    }

    // User verknüpfen falls vorhanden
    if (request.getUserId() != null) {
      User user =
          userRepository
              .findById(request.getUserId())
              .orElseThrow(() -> new IllegalArgumentException("User not found"));
      participant.setUser(user);
    }

    participant = participantRepository.save(participant);

    ParticipantDto dto = mapToDto(participant);
    dto.setCalculatedBudget(budgetCalculationService.calculateParticipantBudget(participant, event));
    return dto;
  }

  @Transactional
  public ParticipantDto updateParticipant(
      Long id, CreateParticipantRequest request, Long userId) {
    Participant participant =
        participantRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

    // Prüfe ob User der Event-Ersteller ist
    if (!participant.getEvent().getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to update this participant");
    }

    participant.setName(request.getName());
    participant.setType(request.getType());
    participant.setCustomBudget(request.getCustomBudget());
    participant.setIsCouple(request.getIsCouple() != null ? request.getIsCouple() : false);

    // Partner verknüpfen falls vorhanden
    if (request.getPartnerId() != null) {
      Participant partner =
          participantRepository
              .findById(request.getPartnerId())
              .orElseThrow(() -> new IllegalArgumentException("Partner not found"));
      participant.setPartner(partner);
      partner.setIsCouple(true);
      partner.setPartner(participant);
      participantRepository.save(partner);
    } else {
      // Partner-Verbindung entfernen
      if (participant.getPartner() != null) {
        Participant oldPartner = participant.getPartner();
        oldPartner.setPartner(null);
        oldPartner.setIsCouple(false);
        participantRepository.save(oldPartner);
      }
      participant.setPartner(null);
    }

    // User verknüpfen falls vorhanden
    if (request.getUserId() != null) {
      User user =
          userRepository
              .findById(request.getUserId())
              .orElseThrow(() -> new IllegalArgumentException("User not found"));
      participant.setUser(user);
    } else {
      participant.setUser(null);
    }

    participant = participantRepository.save(participant);

    ParticipantDto dto = mapToDto(participant);
    dto.setCalculatedBudget(
        budgetCalculationService.calculateParticipantBudget(participant, participant.getEvent()));
    return dto;
  }

  @Transactional
  public void deleteParticipant(Long id, Long userId) {
    Participant participant =
        participantRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

    // Prüfe ob User der Event-Ersteller ist
    if (!participant.getEvent().getCreator().getId().equals(userId)) {
      throw new IllegalArgumentException("Not authorized to delete this participant");
    }

    // Partner-Verbindung entfernen
    if (participant.getPartner() != null) {
      Participant partner = participant.getPartner();
      partner.setPartner(null);
      partner.setIsCouple(false);
      participantRepository.save(partner);
    }

    participantRepository.delete(participant);
  }

  private ParticipantDto mapToDto(Participant participant) {
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
}

