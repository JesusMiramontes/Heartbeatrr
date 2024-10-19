package xyz.miramontes.heartbeatrr.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HealthCheckService {
    @Value("#{'${heartbeatrr.services.urls}'.split(',')}")
    private List<String> servicesUrls;

    private final RetryableHealthCheckService retryableHealthCheckService;

    private final DiscordService discordService;

    public HealthCheckService(
            RetryableHealthCheckService retryableHealthCheckService,
            DiscordService discordService) {
        this.retryableHealthCheckService = retryableHealthCheckService;
        this.discordService = discordService;
    }

    // Loop through all services and check each one
    public void checkAllServices() throws IOException {
        List<String> downServices = new ArrayList<>();
        for (String service : servicesUrls) {
            log.info("Checking service at URL: {}", service);
            ResponseEntity<String> result = retryableHealthCheckService.checkEndpoint(service);
            log.info("Result for service {}: {}", service, result.getBody());
            if (result.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
                downServices.add(service);
            }
        }
        if (!downServices.isEmpty()) notify(downServices);
    }

    private void notify(List<String> downServices) throws IOException {
        discordService.sendDiscordAlert("These services are down: " + downServices.toString());
    }
}
