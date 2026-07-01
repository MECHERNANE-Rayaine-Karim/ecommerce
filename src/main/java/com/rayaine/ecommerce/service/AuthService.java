package com.rayaine.ecommerce.service;

import com.rayaine.ecommerce.exception.UsernameAlreadyExistsException;
import com.rayaine.ecommerce.model.User;
import com.rayaine.ecommerce.repository.UserRepository;
import com.rayaine.ecommerce.security.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register( String username, String password ,String contact, User.Role role ){
        if(userRepository.findByUsername(username).isPresent()){
            throw new UsernameAlreadyExistsException("this username exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setContact(contact);
        userRepository.save(user);
    }

    public String login( String username, String password ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password)
        );
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("user not found")
        );
        return jwtUtil.generateToken(username,user.getRole());
    }



}
