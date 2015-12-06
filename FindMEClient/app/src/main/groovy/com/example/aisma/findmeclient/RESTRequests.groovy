package com.example.aisma.findmeclient

import com.arasthel.swissknife.annotations.OnBackground
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

public class RESTRequests {

    @OnBackground
    public void testRestRequest() {
        final String url = "http://rest-service.guides.spring.io/greeting";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        String response = restTemplate.getForObject(url, Object).toString();
        println response
    }

    @OnBackground
    public void getAllUsers() {
        final String url = "http://10.0.2.2:8080/medi/getOnlineUsers"
    }

}
