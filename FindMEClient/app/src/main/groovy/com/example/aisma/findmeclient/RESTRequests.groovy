package com.example.aisma.findmeclient

import android.util.Log
import com.arasthel.swissknife.annotations.OnBackground
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

public class RESTRequests {

    private static final String SERVER_IP = "http://10.0.2.2:8080"
    private static final String SECRET = "geheim"

    @OnBackground
    public void testRestRequest() {
        final String url = "http://rest-service.guides.spring.io/greeting"
        RestTemplate restTemplate = new RestTemplate()
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter())
        String response = restTemplate.getForObject(url, Object).toString()
        println response
    }

    @OnBackground
    public void getAllUsers(String email) {
        final String url = SERVER_IP + "/medi/getOnlineUsers?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email)
            println response
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void getIpForEmail(String ownEmail, String targetEmail) {
        final String url = SERVER_IP + "/medi/getIP?email={ownEmail}&targetEmail={targetEmail}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, ownEmail, targetEmail)
            println response
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void register(String email, String name) {
        final String url = SERVER_IP + "/auth/register?email={email}&name={name}&secret=" + SECRET
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email, name)
            println response
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void login(String email) {
        final String url = SERVER_IP + "/auth/login?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email)
            println response
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void logout(String email) {
        final String url = SERVER_IP + "/auth/logout?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email)
            println response
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate()
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter())
        return restTemplate
    }

}
