package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.SignupRequest;
import com.example.ecommerce.dto.response.AuthResponse;
import com.example.ecommerce.dto.response.JwtResponse;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.service.authservice.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signUp(signupRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody JwtResponse jwtResponse) {
        return ResponseEntity.ok(authService.refresh(jwtResponse));
    }
    @PostMapping("/isTokenValid")
    public Boolean isTokenValid(@RequestBody String token){
        return authService.isTokenValid(token);
    }
}
