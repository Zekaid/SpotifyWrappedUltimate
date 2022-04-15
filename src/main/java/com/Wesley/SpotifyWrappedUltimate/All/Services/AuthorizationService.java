package com.Wesley.SpotifyWrappedUltimate.All.Services;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.*;
import java.time.LocalTime;
import java.util.*;

@Service
public class AuthorizationService {
    static int curr_time;
    static String refresh_token;
    static String access_token;
    String auth_header_val;
    String client_id = "b21f900d2dba494a8877950764f8171b";
    String client_secret = "8b421e25e6b44a65940a041c55c72bd6";
    String redirect_uri = "https://spotifywrappedultimate.herokuapp.com/callback";
    String scope_user = "user-read-private%20user-read-email%20user-top-read";
    String state;


    //The start of our website
    public ResponseEntity login() throws URISyntaxException {
        //Authorization url format
        int left = 97; // letter 'a'
        int right = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();

        state = random.ints(left, right + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        String url = "https://accounts.spotify.com/authorize?"
                + "client_id=" + client_id
                + "&response_type=code&"
                + "redirect_uri=" + redirect_uri
                + "&scope=" + scope_user
                + "&state=" + state
                + "&show_dialog=true";

        URI uri = new URI(url);

        //Redirects users to the login page from Spotify API
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    //If authorized, redirects to /callback and returns authorization code
    public void AccessToken(String code, RestTemplate rest_template) {
        HttpHeaders headers = new HttpHeaders();
        //Encode information and create required header
        auth_header_val = "Basic " + Base64.getEncoder().encodeToString((client_id+":"+client_secret).getBytes());
        headers.add("Authorization", auth_header_val);

        //Setting content type of header
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Parameters for POST request
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/api/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("redirect_uri", redirect_uri);

        //Make POST request to api token endpoint, and create access token
        HttpEntity<?> entity = new HttpEntity<>(headers);
        HttpEntity<HashMap> response = rest_template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                HashMap.class);

        curr_time = LocalTime.now().toSecondOfDay();
        access_token = response.getBody().get("access_token").toString();
        refresh_token = response.getBody().get("refresh_token").toString();
    }

    public void RefreshToken(RestTemplate restTemplate, HttpHeaders headersR){

        headersR.add("Authorization", auth_header_val);
        headersR.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        UriComponentsBuilder builderR = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/api/token")
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", refresh_token);

        HttpEntity<?> entityR = new HttpEntity<>(headersR);
        HttpEntity<HashMap> responseR = restTemplate.exchange(
                builderR.toUriString(),
                HttpMethod.POST,
                entityR,
                HashMap.class);

        access_token = responseR.getBody().get("access_token").toString();
    }

    public static String GetAccessToken(){
        return access_token;
    }
    public static void Destroy(){
        access_token = null;
    }

    public static String GetRefreshToken(){
        return refresh_token;
    }
    public static int GetCurrTime(){
        return curr_time;
    }
    public static void SetCurrTime(int new_time){
        curr_time = new_time;
    }
}
