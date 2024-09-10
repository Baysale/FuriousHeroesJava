package com.furiousheroes.service;

import com.furiousheroes.model.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public interface UserService {
    public User saveUser(User user);
    public List<User> getAllUsers();
    public User loadUserById(Long userId);
    public User loadUserByEmail(String email) throws NoSuchElementException;
    public User loadUserByUserName(String username);
    public boolean existsByUserName(String username);
    public boolean existsByEmail(String email);
    public void sendPasswordResetLink(String email) throws IOException;
    public boolean resetPassword(String token, String newPassword);
}
