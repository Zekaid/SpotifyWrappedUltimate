package com.Wesley.SpotifyWrappedUltimate.All.Controllers;

import com.Wesley.SpotifyWrappedUltimate.All.Services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

class Pair {
    String artist;
    String track;
    public Pair (String artist, String track){
        this.artist = artist;
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public String getTrack() {
        return track;
    }
}

@RestController
public class TopTracks {

    @Autowired
    AuthorizationService auth;

    @RequestMapping(path = "/api/top/tracks", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Pair>> Metadata(RestTemplate rest_template) {
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
                "https://api.spotify.com/v1/me/top/tracks?time_range=long_term",
                HttpMethod.GET,
                entity,
                HashMap.class);

        ArrayList<Pair> artist_track = new ArrayList<>();
        ArrayList<LinkedHashMap<String, String>> data = ((ArrayList) response.getBody().get("items"));
        for (int i = 0; i < 10; i++) {
            LinkedHashMap<String, ?> map = data.get(i);
            artist_track.add(new Pair(((LinkedHashMap)((ArrayList) map.get("artists")).get(0)).get("name").toString(), map.get("name")+""));
        }
        return new ResponseEntity<>(artist_track, HttpStatus.OK);
    }
}