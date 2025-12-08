package com.example.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(unique = true, nullable = false)
  private String username;

  @NotBlank
  @Email
  @Column(unique = true, nullable = false)
  private String email;

  @NotBlank
  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Event> createdEvents = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Participant> participants = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}

