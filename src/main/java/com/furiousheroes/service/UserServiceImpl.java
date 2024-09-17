package com.furiousheroes.service;

import com.furiousheroes.model.PasswordResetToken;
import com.furiousheroes.model.User;
import com.furiousheroes.repository.PasswordResetTokenRepository;
import com.furiousheroes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User loadUserById(Long userId) throws NoSuchElementException {
        return userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(userId + " does not exist"));
    }
    @Override
    public User loadUserByUserName(String username) throws NoSuchElementException {
        return userRepository.findByUserName(username).orElseThrow(() -> new NoSuchElementException(username + " does not exist"));
    }
    @Override
    public User loadUserByEmail(String email) throws NoSuchElementException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException(email + " does not exist"));
    }
    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendPasswordResetLink(String email) throws IOException {
        User user = loadUserByEmail(email);

        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);

        String token = UUID.randomUUID().toString();
        if (existingToken.isPresent()) {
            PasswordResetToken passwordResetToken = existingToken.get();
            passwordResetToken.setToken(token);
            passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(24L));
            passwordResetTokenRepository.save(passwordResetToken);
        } else {
            PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.save(passwordResetToken);
        }

        String resetUrl = "http://localhost:3000/account/PasswordReset?token=" + token;

        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "To reset your password, click the following link: " + resetUrl);
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);

        if (tokenOptional.isPresent() && !tokenOptional.get().isExpired()) {
            User user = tokenOptional.get().getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
