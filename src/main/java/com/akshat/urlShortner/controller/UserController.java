package com.akshat.urlShortner.controller;


import com.akshat.urlShortner.entity.User;
import com.akshat.urlShortner.service.UrlService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {


    @Autowired
    private UrlService urlService;

    @PostMapping
    public ResponseEntity<@NonNull Object> create(@RequestBody  User user) {
        try {
            User newUser = urlService.createUser(user);
            if (newUser == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception is occur{}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}