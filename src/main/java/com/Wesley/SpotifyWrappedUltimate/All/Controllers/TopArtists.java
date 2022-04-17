package com.Wesley.SpotifyWrappedUltimate.All.Controllers;

import com.Wesley.SpotifyWrappedUltimate.All.Services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

class Single {
    String artist;

    public Single (String artist){
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

}

@RestController
public class TopArtists {

    @Autowired
    AuthorizationService auth;

    @GetMapping("/api/top/artists")
    public ResponseEntity<ArrayList<Single>> Metadata(RestTemplate rest_template){
        int curr_time = LocalTime.now().toSecondOfDay();
        if (curr_time - auth.GetCurrTime() > 3600 || curr_time - auth.GetCurrTime() < 0){
            auth.RefreshToken(new RestTemplate(), new HttpHeaders());
            auth.SetCurrTime(curr_time);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + auth.GetAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<HashMap> response = rest_template.exchange(
                "https://api.spotify.com/v1/me/top/artists?time_range=long_term",
                HttpMethod.GET,
                entity,
                HashMap.class);

        ArrayList<Single> artist = new ArrayList<>();
        ArrayList<LinkedHashMap<String, String>> data = ((ArrayList) response.getBody().get("items"));

        for (int i = 0; i < 10; i++) {
            LinkedHashMap<String, ?> map = data.get(i);
            artist.add(new Single(map.get("name")+""));
        }

        return new ResponseEntity<>(artist, HttpStatus.OK);
    }
}