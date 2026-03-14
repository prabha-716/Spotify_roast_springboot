package com.example.spotify_roast.Controller;

import com.example.spotify_roast.Service.GeminiService;
import com.example.spotify_roast.Service.SpotifyService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/spotify")
public class SpotifyController {
    private final SpotifyService spotifyService;
    private final GeminiService geminiService;

    public SpotifyController(SpotifyService spotifyService,GeminiService geminiService){
        this.spotifyService = spotifyService;
        this.geminiService = geminiService;
    }

    @GetMapping("/top-tracks")
    public String getTopTracks(Model model,@AuthenticationPrincipal OAuth2User user){
        List<Map<String, Object>> tracks = spotifyService.getTopTracks(user);

        model.addAttribute("tracks", tracks);

        return "TopTracks";
    }
    @GetMapping("/saved-tracks")
    public String getSavedTracks(Model model,@AuthenticationPrincipal OAuth2User user){
        List<Map<String,Object>> tracks = spotifyService.getSavedTracks(user);

        model.addAttribute("tracks",tracks);

        return "SavedTracks";
    }
    @GetMapping("/top-artists")
    public String getTopArtists(Model model, @AuthenticationPrincipal OAuth2User user){
        List<Map<String,Object>> artists = spotifyService.getTopArtists(user);
        model.addAttribute("artists", artists);
        return "TopArtists";
    }
    @GetMapping("/roast-data")
    @ResponseBody  // returns plain string to browser for testing
    public String getRoastData(@AuthenticationPrincipal OAuth2User user) {
        return spotifyService.buildRoastPrompt(user);
    }

    @GetMapping("/roast")
    public String getRoast(Model model,@AuthenticationPrincipal OAuth2User user){
        String spotifyData = getRoastData(user);
        String roast = geminiService.getRoast(spotifyData);
        model.addAttribute("roast",roast);
        return "Roast";
    }

}
