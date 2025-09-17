package com.fares7elsadek.syncspace.user.application.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AvatarService {
    private static final String DICEBEAR_API = "https://api.dicebear.com/9.x";
    private static final String[] STYLES = {
            "fun-emoji", "adventurer", "bottts", "croodles"
    };

    private final Random random = new Random();

    public String generateAvatarUrl(String seed) {
        String style = STYLES[random.nextInt(STYLES.length)];
        return String.format("%s/%s/svg?seed=%s", DICEBEAR_API, style, seed);
    }
}
