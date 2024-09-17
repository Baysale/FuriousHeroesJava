package com.furiousheroes.service;

import com.furiousheroes.dto.RegisterDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

@Service
public class OpenAiService {
    private final String pythonApiUrl = "http://127.0.0.1:5000/generate_owl";
    private final RestTemplate restTemplate = new RestTemplate();

    public String generateOwlImage(RegisterDTO registerDTO) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RegisterDTO> requestEntity = new HttpEntity<>(registerDTO, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                pythonApiUrl, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("image_url")) {
                return responseBody.get("image_url");
            } else {
                throw new Exception("Error: No response from image generation API");
            }
        } else {
            throw new Exception("Error: Failed to generate owl image");
        }
    }
}
