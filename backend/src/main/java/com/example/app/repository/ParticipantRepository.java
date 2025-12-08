package com.example.app.repository;

import com.example.app.entity.Event;
import com.example.app.entity.Participant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  List<Participant> findByEvent(Event event);

  List<Participant> findByEventId(Long eventId);

  List<Participant> findByUserId(Long userId);
}

