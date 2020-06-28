package org.users.client;

import org.springframework.web.client.RestClientResponseException;

public class UserClientException extends RuntimeException {

    UserClientException(String message, final RestClientResponseException e) {
        super(message,e);
    }
}
