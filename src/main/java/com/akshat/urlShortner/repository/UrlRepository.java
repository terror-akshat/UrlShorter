package com.akshat.urlShortner.repository;

import com.akshat.urlShortner.entity.Url;
import com.akshat.urlShortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Integer> {
    Optional<Url> findByShortUrl(String shortUrl);
}