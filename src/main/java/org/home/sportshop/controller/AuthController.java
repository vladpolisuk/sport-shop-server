package org.home.sportshop.controller;

import org.home.sportshop.model.dto.AuthRequest;
import org.home.sportshop.model.dto.AuthResponse;
import org.home.sportshop.model.dto.CheckAuthResponse;
import org.home.sportshop.model.dto.UserResponse;
import org.home.sportshop.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "https://vladpolisuk-sport-shop.vercel.app"}, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody AuthRequest request) {
        UserResponse user = authService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<CheckAuthResponse> checkAuth(@RequestHeader(value = "Authorization", required = false) String token) {
        CheckAuthResponse response = authService.checkAuth(token);
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
} 