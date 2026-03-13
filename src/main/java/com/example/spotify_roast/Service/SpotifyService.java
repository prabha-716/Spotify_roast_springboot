package com.example.spotify_roast.Service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.util.List;
import java.util.Map;


@Service
public class SpotifyService {
    private final OAuth2AuthorizedClientService clientService;

    public SpotifyService(OAuth2AuthorizedClientService clientService){
        this.clientService = clientService;
    }

    public List<Map<String,Object>> getTopTracks(OAuth2User user){
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                "spotify",
                user.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://api.spotify.com/v1/me/top/tracks?limit=15";

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );
        return (List<Map<String,Object>>) response.getBody().get("items");
    }

    public List<Map<String,Object>> getSavedTracks(OAuth2User user){
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("spotify",user.getName());
        String accessToken = client.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url ="https://api.spotify.com/v1/me/tracks?limit=15";

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );
        return (List<Map<String,Object>>) response.getBody().get("items");

    }
    public List<Map<String,Object>> getTopArtists(OAuth2User user){
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("spotify", user.getName());
        String accessToken = client.getAccessToken().getTokenValue();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.spotify.com/v1/me/top/artists?limit=15";

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );
        return (List<Map<String,Object>>) response.getBody().get("items");
    }
    public String buildRoastPrompt(OAuth2User user) {
        List<Map<String, Object>> topTracks = getTopTracks(user);
        List<Map<String, Object>> topArtists = getTopArtists(user);
        List<Map<String, Object>> savedTracks = getSavedTracks(user);

        StringBuilder sb = new StringBuilder();

        // Top Artists
        sb.append("Top Artists: ");
        for (Map<String, Object> artist : topArtists) {
            sb.append(artist.get("name")).append(", ");
        }

        // Top Tracks
        sb.append("\nTop Tracks: ");
        for (Map<String, Object> track : topTracks) {
            sb.append(track.get("name")).append(" by ");
            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            sb.append(artists.get(0).get("name")).append(", ");
        }

        // Saved Tracks (remember the extra "track" wrapper!)
        sb.append("\nSaved Tracks: ");
        for (Map<String, Object> item : savedTracks) {
            Map<String, Object> track = (Map<String, Object>) item.get("track");
            sb.append(track.get("name")).append(" by ");
            List<Map<String, Object>> artists = (List<Map<String, Object>>) track.get("artists");
            sb.append(artists.get(0).get("name")).append(", ");
        }

        return sb.toString();
    }
}
