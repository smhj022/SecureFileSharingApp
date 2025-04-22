package com.project.SecureFileSharingApiApplication.controller;


import com.project.SecureFileSharingApiApplication.repo.UserInfo;
import com.project.SecureFileSharingApiApplication.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    private final JwtUtil jwtUtil;

    public UserController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserInfo user){

        if(Objects.equals(user.getUserName(), "suyash") &&
                Objects.equals(user.getPassword(), "password")){

            String token = jwtUtil.generateToken(user.getUserName());

            return new ResponseEntity<>(token, HttpStatus.OK);

        }

        return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
    }

}
