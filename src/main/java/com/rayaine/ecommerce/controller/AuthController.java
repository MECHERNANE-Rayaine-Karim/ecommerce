package com.rayaine.ecommerce.controller;


import com.rayaine.ecommerce.dto.LoginRequest;
import com.rayaine.ecommerce.dto.RegisterRequest;
import com.rayaine.ecommerce.model.User;
import com.rayaine.ecommerce.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request ) {
        authService.register(request.getUsername() , request.getPassword(),request.getContact(), request.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request ) {
        String token = authService.login(request.getUsername(), request.getPassword());
        Map<String,String> respond = new HashMap<>();
        respond.put("token",token);
        return ResponseEntity.ok(respond);
    }


}
