package com.project.SecureFileSharingApiApplication.controller;


import com.project.SecureFileSharingApiApplication.dto.AuthRequest;
import com.project.SecureFileSharingApiApplication.dto.AuthResponse;
import com.project.SecureFileSharingApiApplication.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthenticationService authenticationService;

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
        String token = authenticationService.authenticate(request);
        if(token == null){
            return new ResponseEntity<>(new AuthResponse("ERROR : Invalid User Credentials"),
                    HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new AuthResponse("Successful Login", token));
    }

}
