package com.shopflow.controller;

import com.shopflow.dto.AuthResponse;
import com.shopflow.dto.LoginRequest;
import com.shopflow.dto.RefreshRequest;
import com.shopflow.dto.RegisterRequest;
import com.shopflow.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshRequest request
    ) {
        return ResponseEntity.ok(
                authService.refresh(
                        request.getRefreshToken()
                )
        );
    }
}