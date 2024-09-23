package com.furiousheroes.controller;

import com.furiousheroes.exception.InsufficientResourcesException;
import com.furiousheroes.model.User;
import com.furiousheroes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base")
@CrossOrigin
public class BaseController {

    @Autowired
    private UserService userService;

    @PutMapping("/smithy")
    public ResponseEntity<String> upgradeSmithy() {
        return upgradeStructure("smithy");
    }

    @PutMapping("/barracks")
    public ResponseEntity<String> upgradeBarracks() {
        return upgradeStructure("barracks");
    }

    @PutMapping("/alchemy-brewery")
    public ResponseEntity<String> upgradeAlchemyBrewery() {
        return upgradeStructure("alchemyBrewery");
    }

    @PutMapping("/stall")
    public ResponseEntity<String> upgradeStall() {
        return upgradeStructure("stall");
    }

    private ResponseEntity<String> upgradeStructure(String structureType) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUserName(currentUsername);

        long woodCost;

        switch (structureType) {
            case "smithy":
                woodCost = user.getSmithy().getWoodCost();
                break;
            case "barracks":
                woodCost = user.getBarrack().getWoodCost();
                break;
            case "alchemyBrewery":
                woodCost = user.getAlchemyBrewery().getWoodCost();
                break;
            case "stall":
                woodCost = user.getStall().getWoodCost();
                break;
            default:
                return new ResponseEntity<>("Invalid structure type", HttpStatus.BAD_REQUEST);
        }

        if (user.getWood() < woodCost) {
            throw new InsufficientResourcesException("Not enough wood to upgrade " + structureType);
        }

        user.setWood(user.getWood() - woodCost);
        long newWoodCost = (long) (woodCost * 1.1);

        switch (structureType) {
            case "smithy":
                user.getSmithy().setWoodCost(newWoodCost);
                break;
            case "barracks":
                user.getBarrack().setWoodCost(newWoodCost);
                break;
            case "alchemyBrewery":
                user.getAlchemyBrewery().setWoodCost(newWoodCost);
                break;
            case "stall":
                user.getStall().setWoodCost(newWoodCost);
                break;
        }

        userService.saveUser(user);

        return new ResponseEntity<>(structureType + " upgraded successfully!", HttpStatus.OK);
    }
}
