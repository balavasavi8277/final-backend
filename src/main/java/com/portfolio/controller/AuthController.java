package com.portfolio.controller;

import com.portfolio.entity.User;
import com.portfolio.repo.UserRepo;
import com.portfolio.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // ✅ IMPORTANT IMPORT

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserRepo userRepo;   // ✅ FIXED

    @Autowired
    private JwtUtil jwtUtil;     // ✅ FIXED

    // REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        return userRepo.save(user);
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {

        User existing = userRepo.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!existing.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(existing.getEmail());

        return Map.of(
                "token", token,
                "email", existing.getEmail(),
                "name", existing.getName(),
                "role", existing.getRole()
        );
    }
}