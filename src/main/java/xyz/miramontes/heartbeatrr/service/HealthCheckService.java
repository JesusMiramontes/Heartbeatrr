package xyz.miramontes.heartbeatrr.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthCheckService {
    @Value("#{'${heartbeatrr.services.urls}'.split(',')}")
    private List<String> servicesUrls;

    private final RetryableHealthCheckService retryableHealthCheckService;

    public HealthCheckService(RetryableHealthCheckService retryableHealthCheckService) {
        this.retryableHealthCheckService = retryableHealthCheckService;
    }

    // Loop through all services and check each one
    public void checkAllServices() {
        for (String service : servicesUrls) {
            log.info("Checking service at URL: {}", service);
            ResponseEntity<String> result = retryableHealthCheckService.checkEndpoint(service);
            log.info("Result for service {}: {}", service, result.getBody());
        }
    }
}
