package com.akshat.urlShortner.repository;

import com.akshat.urlShortner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
