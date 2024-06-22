package com.example.ecommerce.service.authservice;

import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.SignupRequest;
import com.example.ecommerce.dto.response.AuthResponse;
import com.example.ecommerce.dto.response.JwtResponse;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    public MessageResponse signUp(SignupRequest signupRequest){
        MessageResponse messageResponse = new MessageResponse();
        try{
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            if(signupRequest.getRole().equals("ADMIN"))
                user.setRole(Role.ADMIN);
            else
                user.setRole(Role.USER);
            User result = userRepository.save(user);
            if(result.getId() > 0){
                messageResponse.setMessage("User registered successfully");
            }
        }
        catch (Exception e){
            messageResponse.setMessage(e.getMessage());
        }
        return messageResponse;
    }

    public AuthResponse login(LoginRequest loginRequest){
        UserResponse userResponse = new UserResponse();
        JwtResponse jwtResponse = new JwtResponse();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            var user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            System.out.println("USER IS: "+ user);
            userResponse.setId(user.getId());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setRole(user.getRole());
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            jwtResponse.setToken(jwt);
            jwtResponse.setRefreshToken(refreshToken);
            jwtResponse.setMessage("Login successful");
        }
        catch (Exception e){
            jwtResponse.setMessage(e.getMessage());
        }
        return new AuthResponse(userResponse, jwtResponse);
    }
    public JwtResponse refresh(JwtResponse response){
        JwtResponse jwtResponse = new JwtResponse();
        String username = jwtUtils.extractUsername(response.getToken());
        User user = userRepository.findByUsername(username).orElseThrow();
        if(jwtUtils.isTokenValid(response.getToken(), user)){
            var jwt = jwtUtils.generateToken(user);
            jwtResponse.setToken(jwt);
            jwtResponse.setRefreshToken(response.getToken());
            jwtResponse.setMessage("Refresh successful");
        }
        return jwtResponse;
    }
    public boolean isTokenValid(String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtils.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtils.isTokenValid(token, userDetails);
    }
}
