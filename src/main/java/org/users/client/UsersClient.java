package org.users.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.users.client.model.User;

public class UsersClient {

    public static final String USER = "/user/";
    public static final String CITY = "/city/";
    public static final String USERS = "/users/";
    private final String rootContext;
    private final RestTemplate restTemplate;

    public UsersClient(String rootContext, RestTemplate restTemplate) {
        this.rootContext = rootContext;
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        final String url = rootContext + USERS;
        final ResponseEntity<User[]> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, getHttpHeaders(), User[].class);
        } catch (RestClientResponseException e) {
            throw new UserClientException("Error processing request", e);
        }
        return Arrays.asList(response.getBody());
    }

    public User getUser(long id) {
        final String url = rootContext + USER + id;
        final ResponseEntity<User> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, getHttpHeaders(), User.class);
        } catch (RestClientResponseException e) {
            throw new UserClientException("Error processing request", e);
        }
        return response.getBody();
    }

    public List<User> getUsersByCity(String city) {
        final String url = rootContext + CITY + city + USERS;
        final ResponseEntity<User[]> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, getHttpHeaders(), User[].class);
        } catch (RestClientResponseException e) {
            throw new UserClientException("Error processing request", e);
        }
        return Arrays.asList(response.getBody());
    }

    private HttpEntity<String> getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);
        return new HttpEntity<>(headers);
    }

}
