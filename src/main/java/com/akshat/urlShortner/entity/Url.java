package com.akshat.urlShortner.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;


@Entity
@Data
public class Url {

    @Id
    private String shortUrl;

    @Column(nullable = false)
    private String longUrl;

    private Date createdTime;
    private Date expirationTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Url() {}
}