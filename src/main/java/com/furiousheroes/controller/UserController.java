package com.furiousheroes.controller;

import com.furiousheroes.dto.UpdateUserDTO;
import com.furiousheroes.model.User;
import com.furiousheroes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUserName(currentUsername);

        if (userService.existsByUserName(updateUserDTO.getNewUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        user.setUserName(updateUserDTO.getNewUsername());
        user.setPassword(passwordEncoder.encode(updateUserDTO.getNewPassword()));
        user.setEmail(updateUserDTO.getNewEmail());

        userService.saveUser(user);

        return new ResponseEntity<>("User updated successfully!", HttpStatus.OK);
    }
}
