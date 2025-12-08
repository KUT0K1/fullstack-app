package com.example.app.repository;

import com.example.app.entity.Event;
import com.example.app.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
  List<Event> findByCreator(User creator);

  List<Event> findByCreatorId(Long creatorId);
}

