package com.fares7elsadek.syncspace.channel.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class YoutubeService {
    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos";

    public Map<String,Object> getVideoInfo(String videoId){
        Map<String, Object> response = getVideoDetails(videoId);
        if (response != null && response.containsKey("items")) {
            var items = (java.util.List<?>) response.get("items");
            if (!items.isEmpty()) {
                var item = (Map<?, ?>) items.get(0);
                var snippet = (Map<?, ?>) item.get("snippet");

                Map<String, Object> result = new HashMap<>();
                result.put("title", snippet.get("title"));
                var thumbnails = (Map<?, ?>) snippet.get("thumbnails");
                if (thumbnails != null && thumbnails.containsKey("high")) {
                    var high = (Map<?, ?>) thumbnails.get("high");
                    result.put("thumbnail", high.get("url"));
                }

                return result;
            }
        }
        return Map.of("error", "Video not found");
    }

    private Map<String, Object> getVideoDetails(String videoId) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_URL)
                .queryParam("id", videoId)
                .queryParam("part", "snippet")
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
