package xyz.miramontes.heartbeatrr.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckService {

    @Value("${connection.timeout}")
    private int connectionTimeout;

    @Value("${read.timeout}")
    private int readTimeout;

    @Value("#{'${services}'.split(',')}")
    private List<String> services;

    public void checkServices() {
        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();

        for (String endpoint : services) {
            ResponseEntity<String> checkedEndpoint = checkEndpoint(endpoint);
            if (checkedEndpoint.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
                fail.add(endpoint + ": " + checkedEndpoint.getBody());
            } else {
                success.add(endpoint + ": " + checkedEndpoint.getBody());
            }
        }

        System.out.println("fail = " + fail.toString());
        System.out.println("success = " + success.toString());
    }

    private ResponseEntity<String> checkEndpoint(String endpointUrl) {
        try {
            RestTemplate customRestTemplate =
                    getRestTemplateWithTimeout(connectionTimeout, readTimeout);

            // Make the GET request
            ResponseEntity<String> response =
                    customRestTemplate.getForEntity(endpointUrl, String.class);

            // Return the HTTP status
            return new ResponseEntity<>(
                    "Response status: " + response.getStatusCode(), response.getStatusCode());

        } catch (ResourceAccessException e) {
            return new ResponseEntity<>(
                    "Unable to connect: " + e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (HttpClientErrorException e) {
            // Handle connection failures or other exceptions
            return new ResponseEntity<>("Unable to connect: " + e.getMessage(), e.getStatusCode());
        }
    }

    // Method to configure RestTemplate with timeouts
    private RestTemplate getRestTemplateWithTimeout(int connectionTimeout, int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout); // Set connection timeout
        factory.setReadTimeout(readTimeout); // Set read timeout
        return new RestTemplate(factory);
    }
}
