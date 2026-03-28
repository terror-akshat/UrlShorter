package com.akshat.urlShortner.service;

import com.akshat.urlShortner.entity.Url;
import com.akshat.urlShortner.entity.User;
import com.akshat.urlShortner.repository.UrlRepository;
import com.akshat.urlShortner.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

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
        try {
            String longUrl = redisService.get(shortUrl);
            if (longUrl == null || longUrl.isBlank()) {
                String url = urlRepository.findByShortUrl(shortUrl)
                        .orElseThrow(() -> new RuntimeException("Url not found"))
                        .getLongUrl();
                if (url != null && !url.isBlank()) {
                    redisService.set(shortUrl, url, 3000L);
                    return url;
                }
            }
            return longUrl;
        } catch (Exception e) {
            log.error("Exception{}", String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}