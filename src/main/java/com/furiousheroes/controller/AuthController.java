package com.furiousheroes.controller;

import com.furiousheroes.config.JWTGenerator;
import com.furiousheroes.dto.AuthResponseDTO;
import com.furiousheroes.dto.LoginDTO;
import com.furiousheroes.dto.RegisterDTO;
import com.furiousheroes.model.*;
import com.furiousheroes.repository.RoleRepository;
import com.furiousheroes.service.JwtBlacklistService;
import com.furiousheroes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        if(userService.existsByUserName(registerDTO.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        if(userService.existsByEmail(registerDTO.getEmail())) {
            return new ResponseEntity<>("Email is taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUserName(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setSmithy(Smithy.defaultSmithy());
        user.setAlchemyBrewery(AlchemyBrewery.defaultAlchemyBrewery());
        user.setBarrack(Barrack.defaultBarrack());
        user.setStall(Stall.defaultStall());

        roleRepository.findByName("USER").ifPresent(role -> user.setRoles(Collections.singletonList(role)));

        userService.saveUser(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        Long userId = userService.loadUserByUserName(loginDTO.getUsername()).getId();
        return new ResponseEntity<>(new AuthResponseDTO(userId, token), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtBlacklistService.extractTokenFromRequest(request);
        jwtBlacklistService.addToBlacklist(token);
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("User logged out successfully!", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws IOException {
        userService.sendPasswordResetLink(email);
        return new ResponseEntity<>("Password reset link sent!", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        boolean reset = userService.resetPassword(token, newPassword);
        if(reset) {
            return new ResponseEntity<>("Password reset successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }
    }
}
