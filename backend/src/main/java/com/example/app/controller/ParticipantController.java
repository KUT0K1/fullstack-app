package com.example.app.controller;

import com.example.app.dto.CreateParticipantRequest;
import com.example.app.dto.ParticipantDto;
import com.example.app.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ParticipantController {

  private final com.example.app.service.ParticipantService participantService;

  @PostMapping
  public ResponseEntity<ParticipantDto> createParticipant(
      @PathVariable Long eventId,
      @Valid @RequestBody CreateParticipantRequest request,
      @CurrentUser Long userId) {
    try {
      ParticipantDto participant = participantService.createParticipant(eventId, request, userId);
      return ResponseEntity.status(HttpStatus.CREATED).body(participant);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<ParticipantDto> updateParticipant(
      @PathVariable Long id,
      @Valid @RequestBody CreateParticipantRequest request,
      @CurrentUser Long userId) {
    try {
      ParticipantDto participant = participantService.updateParticipant(id, request, userId);
      return ResponseEntity.ok(participant);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteParticipant(
      @PathVariable Long id, @CurrentUser Long userId) {
    try {
      participantService.deleteParticipant(id, userId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}

