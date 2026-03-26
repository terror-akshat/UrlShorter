package com.akshat.urlShortner.service;


import com.akshat.urlShortner.entity.Url;
import com.akshat.urlShortner.entity.User;
import com.akshat.urlShortner.repository.UrlRepository;
import com.akshat.urlShortner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public String longToShort(String longUrl) {
        String shortUrl;
        do {
            shortUrl = UUID.randomUUID().toString().substring(0, 8);
        } while (urlRepository.existsById(shortUrl));
        return shortUrl;
    }

    public Url createShortUrl(String longUrl, int id) {
        String shortUrl = longToShort(longUrl);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Url url = new Url();
        url.setShortUrl(shortUrl);
        url.setLongUrl(longUrl);
        url.setCreatedTime(new Date());
        url.setUser(user);
        return urlRepository.save(url);
    }

    public String getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Url not found"))
                .getLongUrl();
    }
}