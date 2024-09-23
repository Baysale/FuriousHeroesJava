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
    public ResponseEntity<User> upgradeSmithy() {
        return upgradeStructure("smithy");
    }

    @PutMapping("/barracks")
    public ResponseEntity<User> upgradeBarracks() {
        return upgradeStructure("barracks");
    }

    @PutMapping("/alchemy-brewery")
    public ResponseEntity<User> upgradeAlchemyBrewery() {
        return upgradeStructure("alchemyBrewery");
    }

    @PutMapping("/stall")
    public ResponseEntity<User> upgradeStall() {
        return upgradeStructure("stall");
    }

    private ResponseEntity<User> upgradeStructure(String structureType) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.loadUserByUserName(currentUsername);

        long woodCost = getStructureWoodCost(user, structureType);

        if (user.getWood() < woodCost) {
            throw new InsufficientResourcesException("Not enough wood to upgrade " + structureType);
        }

        user.setWood(user.getWood() - woodCost);
        increaseStructureWoodCost(user, structureType, woodCost);

        userService.saveUser(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private long getStructureWoodCost(User user, String structureType) {
        switch (structureType) {
            case "smithy":
                return user.getSmithy().getWoodCost();
            case "barracks":
                return user.getBarrack().getWoodCost();
            case "alchemyBrewery":
                return user.getAlchemyBrewery().getWoodCost();
            case "stall":
                return user.getStall().getWoodCost();
            default:
                throw new IllegalArgumentException("Invalid structure type: " + structureType);
        }
    }

    private void increaseStructureWoodCost(User user, String structureType, long woodCost) {
        long newWoodCost = (long) (woodCost * 1.1);

        switch (structureType) {
            case "smithy":
                user.getSmithy().setWoodCost(newWoodCost);
                user.getSmithy().setLevel(user.getSmithy().getLevel()+1);
                break;
            case "barracks":
                user.getBarrack().setWoodCost(newWoodCost);
                user.getBarrack().setLevel(user.getBarrack().getLevel()+1);
                break;
            case "alchemyBrewery":
                user.getAlchemyBrewery().setWoodCost(newWoodCost);
                user.getAlchemyBrewery().setLevel(user.getAlchemyBrewery().getLevel()+1);
                break;
            case "stall":
                user.getStall().setWoodCost(newWoodCost);
                user.getStall().setLevel(user.getStall().getLevel());
                break;
            default:
                throw new IllegalArgumentException("Invalid structure type: " + structureType);
        }
    }
}
