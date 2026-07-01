package com.Rayaine.ecommerce.service;

import com.rayaine.ecommerce.exception.UsernameAlreadyExistsException;
import com.rayaine.ecommerce.model.User;
import com.rayaine.ecommerce.repository.UserRepository;
import com.rayaine.ecommerce.security.JwtUtil;
import com.rayaine.ecommerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameAlreadyExists_throwsException(  ) {
        User someUser = new User();
        someUser.setUsername("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(someUser));
        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class,()-> {
            authService.register("username", "password", null, User.Role.USER);
        });
        assertEquals( "this username exists",exception.getMessage());
    }

    @Test
    void register_whenUsernameIsNew_savesUserWithEncodedPassword( ) {
        String username = "newUser";
        String rawPassword = "password";
        String hashedPassword ="password123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
        authService.register(username, rawPassword, null, User.Role.USER);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));

    }

    @Test
    void login_whenAuthenticateFails_throwsException(){
       String username = "username";
       String password = "password";

       when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("username or password invalid"));

        BadCredentialsException exception  = assertThrows(BadCredentialsException.class,()-> {
            authService.login(username, password);
       });
       assertEquals( "username or password invalid",exception.getMessage());
    }

    @Test
    void login_whenAuthenticatePass_tokenGetGenerated(){
        String username = "username";
        String password = "password";
        User.Role role = User.Role.USER;
        Authentication mockAuth = mock(Authentication.class);
        String expectedToken = "returnedToken";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtil.generateToken(username,role)).thenReturn(expectedToken);
        String returnedToken = authService.login(username, password);
        assertEquals(expectedToken,returnedToken);
    }
}


