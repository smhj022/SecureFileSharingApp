package com.project.SecureFileSharingApiApplication.service;

import com.project.SecureFileSharingApiApplication.dto.AuthRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.project.SecureFileSharingApiApplication.model.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> userMap = new HashMap<>();

    private UserService(){
        userMap.put("admin", new User("admin", "password", "ADMIN"));
        userMap.put("user", new User("user", "password", "USER"));
    }

    public User getUserByUsername(String username){
        try{
            return userMap.get(username);
        } catch (Exception e){
            return null;
        }
    }
}
