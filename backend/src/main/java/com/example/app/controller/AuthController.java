package com.example.app.controller;

import com.example.app.dto.LoginRequest;
import com.example.app.dto.LoginResponse;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.UserDto;
import com.example.app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
    try {
      UserDto user = userService.register(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(user);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    try {
      LoginResponse response = userService.login(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}

