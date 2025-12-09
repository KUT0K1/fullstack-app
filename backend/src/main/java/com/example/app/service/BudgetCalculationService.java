package com.example.app.service;

import com.example.app.entity.Event;
import com.example.app.entity.Participant;
import com.example.app.entity.Participant.ParticipantType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BudgetCalculationService {

  public BigDecimal calculateParticipantBudget(Participant participant, Event event) {
    // Wenn individuelles Budget gesetzt ist, verwende dieses
    if (participant.getCustomBudget() != null) {
      BigDecimal budget = participant.getCustomBudget();
      // Wenn Teil eines Paares, teile durch 2
      if (Boolean.TRUE.equals(participant.getIsCouple()) && participant.getPartner() != null) {
        return budget.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
      }
      return budget;
    }

    // Ansonsten verwende Standard-Budget basierend auf Typ
    BigDecimal standardBudget =
        participant.getType() == ParticipantType.ADULT
            ? event.getAdultBudget()
            : event.getChildBudget();

    // Wenn Teil eines Paares, teile durch 2
    if (Boolean.TRUE.equals(participant.getIsCouple()) && participant.getPartner() != null) {
      return standardBudget.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    return standardBudget;
  }

  public BigDecimal calculateTotalBudget(Event event, List<Participant> participants) {
    BigDecimal total = BigDecimal.ZERO;

    // Budget für alle Teilnehmer berechnen
    for (Participant participant : participants) {
      total = total.add(calculateParticipantBudget(participant, event));
    }

    // Allgemeine Kosten hinzufügen
    if (event.getGeneralCosts() != null) {
      total = total.add(event.getGeneralCosts());
    }

    return total.setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal calculateBudgetPerPayer(
      Event event, List<Participant> participants, List<Participant> payers) {
    BigDecimal totalBudget = calculateTotalBudget(event, participants);

    if (payers == null || payers.isEmpty()) {
      // Wenn keine Payers definiert, verwende alle Erwachsenen
      payers =
          participants.stream()
              .filter(p -> p.getType() == ParticipantType.ADULT)
              .collect(Collectors.toList());
    }

    if (payers.isEmpty()) {
      return BigDecimal.ZERO;
    }

    // Anzahl der Payers berechnen (Paare zählen als 1)
    int payerCount = 0;
    Map<Long, Boolean> counted = new java.util.HashMap<>();

    for (Participant payer : payers) {
      if (Boolean.TRUE.equals(payer.getIsCouple()) && payer.getPartner() != null) {
        Long partnerId = payer.getPartner().getId();
        Long payerId = payer.getId();
        // Paar zählt nur einmal
        if (!counted.containsKey(partnerId) && !counted.containsKey(payerId)) {
          payerCount++;
          counted.put(payerId, true);
          counted.put(partnerId, true);
        }
      } else if (Boolean.TRUE.equals(payer.getIsCouple()) && payer.getPartner() == null) {
        // Paar ohne Partner - zählt als 1
        if (!counted.containsKey(payer.getId())) {
          payerCount++;
          counted.put(payer.getId(), true);
        }
      } else {
        // Einzelperson
        payerCount++;
      }
    }

    if (payerCount == 0) {
      return BigDecimal.ZERO;
    }

    return totalBudget.divide(BigDecimal.valueOf(payerCount), 2, RoundingMode.HALF_UP);
  }

  public int calculateNumberOfPayers(List<Participant> payers) {
    if (payers == null || payers.isEmpty()) {
      return 0;
    }

    int payerCount = 0;
    Map<Long, Boolean> counted = new java.util.HashMap<>();

    for (Participant payer : payers) {
      if (Boolean.TRUE.equals(payer.getIsCouple()) && payer.getPartner() != null) {
        Long partnerId = payer.getPartner().getId();
        Long payerId = payer.getId();
        if (!counted.containsKey(partnerId) && !counted.containsKey(payerId)) {
          payerCount++;
          counted.put(payerId, true);
          counted.put(partnerId, true);
        }
      } else if (Boolean.TRUE.equals(payer.getIsCouple()) && payer.getPartner() == null) {
        if (!counted.containsKey(payer.getId())) {
          payerCount++;
          counted.put(payer.getId(), true);
        }
      } else {
        payerCount++;
      }
    }

    return payerCount;
  }
}

