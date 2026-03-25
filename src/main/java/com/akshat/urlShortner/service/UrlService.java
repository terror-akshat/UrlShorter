package com.akshat.urlShortner.service;


import com.akshat.urlShortner.entity.Url;
import com.akshat.urlShortner.entity.User;
import com.akshat.urlShortner.repository.UrlRepository;
import com.akshat.urlShortner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return "www.akshat." + longUrl.substring(longUrl.length() / 2);
    }

    public Url createShortUrl(String longUrl, int id) {
        String shortUrl = longToShort(longUrl);
        Optional<Url> user = Optional.of(urlRepository.findById(id).orElseThrow(() -> new RuntimeException("User not Found")));

        Url url = new Url();
        url.setShortUrl(shortUrl);
        url.setLongUrl(longUrl);
        url.setUserId(id);
        urlRepository.save(url);
        return url;
    }

    public String getOriginalUrl(String shortUrl) {
        return String.valueOf(urlRepository.findByShortUrl(shortUrl).orElseThrow(() -> new RuntimeException("Url not found")));
    }
}