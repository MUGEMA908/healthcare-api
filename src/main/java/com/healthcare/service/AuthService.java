package com.healthcare.service;

import com.healthcare.dto.AuthResponse;
import com.healthcare.dto.LoginRequest;
import com.healthcare.dto.RegisterRequest;
import com.healthcare.entity.User;
import com.healthcare.exception.ApiException;
import com.healthcare.repository.UserRepository;
import com.healthcare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ApiException("Email already registered");

        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid role. Must be: ADMIN, DOCTOR, PATIENT, NURSE, PHARMACIST");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(role);
        userRepository.save(user);

        return new AuthResponse("Registration successful",
                jwtUtil.generateToken(user.getEmail(), user.getRole().name()),
                user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password"));

        if (!Boolean.TRUE.equals(user.getEnabled()))
            throw new ApiException("Account is disabled. Contact administrator.");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new ApiException("Invalid email or password");

        return new AuthResponse("Login successful",
                jwtUtil.generateToken(user.getEmail(), user.getRole().name()),
                user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
