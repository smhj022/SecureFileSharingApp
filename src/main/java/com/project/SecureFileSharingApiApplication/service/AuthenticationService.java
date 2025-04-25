package com.project.SecureFileSharingApiApplication.service;

import com.project.SecureFileSharingApiApplication.dto.AuthRequest;
import com.project.SecureFileSharingApiApplication.model.User;
import com.project.SecureFileSharingApiApplication.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(AuthRequest request) {
        User user = userService.getUserByUsername(request.getUsername());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return null;
        }
        return jwtUtil.generateToken(request.getUsername(), "ADMIN");

    }
}
