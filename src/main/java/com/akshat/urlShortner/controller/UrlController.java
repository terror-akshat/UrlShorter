package com.akshat.urlShortner.controller;


import com.akshat.urlShortner.entity.Url;
import com.akshat.urlShortner.service.UrlService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/url")
@RestController
@Slf4j
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/user/{id}")
    public ResponseEntity<@NonNull Object> createShortUrl(@RequestBody UrlRequest request, @PathVariable int id) {
        try {
            Url url = urlService.createShortUrl(request.longUrl(), id);
            if (url == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception is occur{}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/{shortUrl}")
    public ResponseEntity<@NonNull Object> redirect(@PathVariable String shortUrl) {
        try {
            String url = urlService.getOriginalUrl(shortUrl);
            if (url == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity
                    .status(HttpStatus.FOUND) // 302 redirect
                    .header(HttpHeaders.LOCATION, url)
                    .build();
        } catch (Exception e) {
            log.error("Exception is occur{}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    public record UrlRequest(String longUrl) {
    }
}