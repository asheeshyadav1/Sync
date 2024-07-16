package com.cmpt213.finalProject.SYNC.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class ImgurService {

    @Value("${imgur.client.id}")
    private String clientId;

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";

    public String uploadImage(MultipartFile image) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + clientId);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(image.getBytes(), headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(IMGUR_UPLOAD_URL, HttpMethod.POST, requestEntity, String.class);

            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getJSONObject("data").getString("link");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to Imgur", e);
        }
    }
}