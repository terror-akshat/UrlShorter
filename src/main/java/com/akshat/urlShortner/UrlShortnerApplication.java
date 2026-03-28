package com.akshat.urlShortner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlShortnerApplication {
	public static void main(String[] args) {
		SpringApplication.run(UrlShortnerApplication.class, args);
	}

}
//
//
//
//http_server_requests_seconds_count{error="none",exception="none",method="GET",outcome="REDIRECTION",status="302",uri="/url/{shortUrl}"} 2
//http_server_requests_seconds_sum{error="none",exception="none",method="GET",outcome="REDIRECTION",status="302",uri="/url/{shortUrl}"} 1.0954827