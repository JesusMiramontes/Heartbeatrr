package xyz.miramontes.heartbeatrr.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class HealthCheckService {

    @Value("${connection.timeout}")
    private int connectionTimeout;

    @Value("${retry.backoff.delay}")
    public long retryBackoffDelay;

    @Value("#{'${services}'.split(',')}")
    private List<String> services;

    @Retryable(
            retryFor = {ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delayExpression = "#{@healthCheckService.retryBackoffDelay}"))
    public ResponseEntity<String> checkEndpoint(String endpointUrl) {
        try {
            RestTemplate restTemplate =
                    getRestTemplateWithTimeout(connectionTimeout); // 3 seconds timeout
            ResponseEntity<String> response = restTemplate.getForEntity(endpointUrl, String.class);

            return onSuccess(endpointUrl, response);

        } catch (ResourceAccessException e) {
            onFail(endpointUrl, e);
            throw e; // Re-throw the exception to trigger retry
        }
    }

    private static void onFail(String endpointUrl, ResourceAccessException e) {
        log.error("Unable to connect to {}: {}", endpointUrl, e.getMessage());
    }

    private static ResponseEntity<String> onSuccess(
            String endpointUrl, ResponseEntity<String> response) {
        log.info("Received status {} from {}", response.getStatusCode(), endpointUrl);

        // Return success, regardless of the status code
        return new ResponseEntity<>(
                "Service is alive with status: " + response.getStatusCode(),
                response.getStatusCode());
    }

    // Method to configure RestTemplate with timeouts
    private RestTemplate getRestTemplateWithTimeout(int timeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout); // Set connection timeout
        factory.setReadTimeout(timeout); // Set read timeout
        return new RestTemplate(factory);
    }

    // Recover method to handle retries after maxAttempts has been reached
    @Recover
    public ResponseEntity<String> recover(ResourceAccessException e, String endpointUrl) {
        log.error("Failed to connect to {} after retries: {}", endpointUrl, e.getMessage());
        return new ResponseEntity<>(
                "Failed to connect to " + endpointUrl + " after retries",
                HttpStatus.SERVICE_UNAVAILABLE);
    }
}
