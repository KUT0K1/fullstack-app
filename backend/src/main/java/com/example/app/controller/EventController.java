package com.example.app.controller;

import com.example.app.dto.CreateEventRequest;
import com.example.app.dto.EventDto;
import com.example.app.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {

  private final com.example.app.service.EventService eventService;

  @PostMapping
  public ResponseEntity<EventDto> createEvent(
      @Valid @RequestBody CreateEventRequest request, @CurrentUser Long userId) {
    try {
      EventDto event = eventService.createEvent(request, userId);
      return ResponseEntity.status(HttpStatus.CREATED).body(event);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @GetMapping
  public ResponseEntity<List<EventDto>> getEvents(@CurrentUser Long userId) {
    List<EventDto> events = eventService.getEventsByCreator(userId);
    return ResponseEntity.ok(events);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EventDto> getEvent(@PathVariable Long id, @CurrentUser Long userId) {
    try {
      EventDto event = eventService.getEventById(id, userId);
      return ResponseEntity.ok(event);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<EventDto> updateEvent(
      @PathVariable Long id,
      @Valid @RequestBody CreateEventRequest request,
      @CurrentUser Long userId) {
    try {
      EventDto event = eventService.updateEvent(id, request, userId);
      return ResponseEntity.ok(event);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @CurrentUser Long userId) {
    try {
      eventService.deleteEvent(id, userId);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}

