package com.furiousheroes.service;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtBlacklistService {
    public void addToBlacklist(String token);
    public boolean isTokenBlacklisted(String token);
    public String extractTokenFromRequest(HttpServletRequest request);
}
