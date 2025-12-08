package com.example.app.repository;

import com.example.app.entity.Event;
import com.example.app.entity.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
  List<Payment> findByEvent(Event event);

  List<Payment> findByEventId(Long eventId);
}

