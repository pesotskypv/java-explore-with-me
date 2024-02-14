package ru.practicum.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T, K> ResponseEntity<Object> post(String path, T body, K response) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, response);
    }

    protected <K> ResponseEntity<K> get(String path, Map<String, Object> parameters, K response) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, response);
    }

    private <T, K> ResponseEntity<K> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters,
                                                        @Nullable T body, K response) {
        assert body != null;
        HttpEntity<T> requestEntity = new HttpEntity<>(body);
        Class responseType = response.getClass();
        ResponseEntity<K> responseEntity;

        try {
            if (parameters != null) {
                responseEntity = rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                responseEntity = rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            return (ResponseEntity<K>) ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        return prepareGatewayResponse(responseEntity);
    }

    private static <T> ResponseEntity<T> prepareGatewayResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
