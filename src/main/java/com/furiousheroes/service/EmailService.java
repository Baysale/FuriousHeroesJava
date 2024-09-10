package com.furiousheroes.service;

import java.io.IOException;

public interface EmailService {
    public void sendEmail(String to, String subject, String body) throws IOException;
}
