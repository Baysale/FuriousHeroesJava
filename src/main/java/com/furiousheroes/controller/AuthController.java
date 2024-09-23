package com.furiousheroes.controller;

import com.furiousheroes.config.JWTGenerator;
import com.furiousheroes.dto.AuthResponseDTO;
import com.furiousheroes.dto.LoginDTO;
import com.furiousheroes.dto.RegisterDTO;
import com.furiousheroes.model.*;
import com.furiousheroes.repository.RoleRepository;
import com.furiousheroes.service.JwtBlacklistService;
import com.furiousheroes.service.OpenAiService;
import com.furiousheroes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO) {
        if (userService.existsByUserName(registerDTO.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(registerDTO.getEmail())) {
            return new ResponseEntity<>("Email is taken!", HttpStatus.BAD_REQUEST);
        }

        try {
            String imageUrl = openAiService.generateOwlImage(registerDTO);

            User user = new User();
            user.setUserName(registerDTO.getUsername());
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            user.setEmail(registerDTO.getEmail());
            user.setGold(1000);
            user.setWood(2000);
            user.setHerbs(1000);
            user.setMetals(1000);
            user.setSmithy(Smithy.defaultSmithy());
            user.setAlchemyBrewery(AlchemyBrewery.defaultAlchemyBrewery());
            user.setBarrack(Barrack.defaultBarrack());
            user.setStall(Stall.defaultStall());
            user.setType(registerDTO.getType());
            user.setAvatarImage(imageUrl);

            roleRepository.findByName("USER").ifPresent(role -> user.setRoles(Collections.singletonList(role)));

            userService.saveUser(user);

            return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        User user = userService.loadUserByUserName(loginDTO.getUsername());
        return new ResponseEntity<>(new AuthResponseDTO(user.getId(), token, user.getAvatarImage()), HttpStatus.OK);
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
