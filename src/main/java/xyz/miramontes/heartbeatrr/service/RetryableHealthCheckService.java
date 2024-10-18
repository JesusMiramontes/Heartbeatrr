package xyz.miramontes.heartbeatrr.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for checking the health status of an endpoint. Uses programmatic retry
 * mechanisms for handling transient failures.
 */
@Service
@Slf4j
public class RetryableHealthCheckService {

    private final RetryTemplate retryTemplate;

    @Value("${connection.timeout}")
    private int connectionTimeout;

    public RetryableHealthCheckService(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    /**
     * Checks the health of the specified endpoint with retry logic for transient network failures.
     *
     * @param endpointUrl The URL of the service to check.
     * @return ResponseEntity containing the status of the service.
     */
    public ResponseEntity<String> checkEndpoint(String endpointUrl) {
        return retryTemplate.execute(
                context -> {
                    try {
                        // RestTemplate with a configured timeout
                        RestTemplate restTemplate = getRestTemplateWithTimeout(connectionTimeout);
                        // Make the GET request and handle success
                        ResponseEntity<String> response =
                                restTemplate.getForEntity(endpointUrl, String.class);
                        return handleSuccess(endpointUrl, response);
                    } catch (ResourceAccessException e) {
                        // Handle failure and re-throw to trigger retry
                        handleFailure(endpointUrl, e);
                        throw e;
                    } catch (HttpClientErrorException e) {
                        // Handle client errors like 4xx responses
                        return handleClientError(endpointUrl, e);
                    }
                },
                context -> handleRecover(endpointUrl));
    }

    /** Handles the case when the service is successfully reachable. */
    private ResponseEntity<String> handleSuccess(
            String endpointUrl, ResponseEntity<String> response) {
        log.info("Received status {} from {}", response.getStatusCode(), endpointUrl);
        return new ResponseEntity<>(
                "Service is alive with status: " + response.getStatusCode(),
                response.getStatusCode());
    }

    /** Handles failures when a network error occurs, logs the error for monitoring. */
    private void handleFailure(String endpointUrl, ResourceAccessException exception) {
        log.error("Unable to connect to {}: {}", endpointUrl, exception.getMessage());
    }

    /** Handles client-side errors (4xx responses) from the service. */
    private ResponseEntity<String> handleClientError(
            String endpointUrl, HttpClientErrorException exception) {
        log.warn("Client error on {}: {}", endpointUrl, exception.getStatusCode());
        return new ResponseEntity<>(
                "Service responded with error: " + exception.getStatusCode(),
                exception.getStatusCode());
    }

    /** Handles fallback logic when retries are exhausted. */
    private ResponseEntity<String> handleRecover(String endpointUrl) {
        log.error("Failed to connect to {} after retries", endpointUrl);
        return new ResponseEntity<>(
                "Failed to connect to " + endpointUrl + " after retries",
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    /** Creates a RestTemplate instance with connection and read timeouts. */
    private RestTemplate getRestTemplateWithTimeout(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout); // Set the connection timeout
        factory.setReadTimeout(timeout); // Set the read timeout
        return new RestTemplate(factory);
    }
}
