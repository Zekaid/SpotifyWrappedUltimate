package com.Wesley.SpotifyWrappedUltimate.All.Controllers;

import com.Wesley.SpotifyWrappedUltimate.All.Services.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.net.*;

@RestController
public class Authorization {

    @Autowired
    AuthorizationService auth;

    @GetMapping("/login")
    public ResponseEntity login() throws URISyntaxException {
        return auth.login();
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ModelAndView AccessToken(@RequestParam("code") String code, RestTemplate rest_template, ModelMap model) throws URISyntaxException {
        auth.AccessToken(code, rest_template);
        model.addAttribute("attribute", "redirectWithRedirectPrefix");
        return new ModelAndView("redirect:/info.html", model);
    }

}