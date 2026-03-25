package com.akshat.urlShortner.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NonNull;
import java.util.Date;


@Entity
@Data
public class Url {


    @NonNull
    private String longUrl;
    @Id
    private String ShortUrl;
    private Date Created_time;
    private Date Expiration_time;
    private int userId;

    public Url() {

    }
}