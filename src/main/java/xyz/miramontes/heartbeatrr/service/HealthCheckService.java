package xyz.miramontes.heartbeatrr.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.miramontes.heartbeatrr.util.UtilMethods;

@Service
@Slf4j
public class HealthCheckService {

    // Inject service URLs from application properties
    @Value("#{${heartbeatrr.services.urls}}")
    private Map<String, String> serviceUrls;

    private final RetryableHealthCheckService retryableHealthCheckService;
    private final DiscordService discordService;

    public HealthCheckService(
            RetryableHealthCheckService retryableHealthCheckService,
            DiscordService discordService) {
        this.retryableHealthCheckService = retryableHealthCheckService;
        this.discordService = discordService;
    }

    @PostConstruct
    private void sendInitialDiscordNotification() throws IOException {
        discordService.sendDiscordAlert(
                "These services will be monitored: " + UtilMethods.convertMapToString(serviceUrls));
    }

    /**
     * Checks the health of all registered services. If any service is down, it will notify via
     * Discord.
     */
    public void checkAllServices() throws IOException {
        List<String> downServices = new ArrayList<>();

        // Iterate through all services and check their health status
        serviceUrls.forEach(
                (serviceName, serviceUrl) -> {
                    log.debug("Checking service: {} at URL: {}", serviceName, serviceUrl);

                    ResponseEntity<String> response =
                            retryableHealthCheckService.checkEndpoint(serviceUrl);

                    log.debug("Response for {}: {}", serviceName, response.getBody());

                    // If the service is unavailable, add it to the list of down services
                    if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                        downServices.add(serviceName + " (" + serviceUrl + ")");
                    }
                });

        // If there are any down services, send a notification
        if (!downServices.isEmpty()) {
            sendAlertForDownServices(downServices);
        } else {
            log.info("All services are alive");
        }
    }

    /**
     * Sends a Discord notification with a list of down services.
     *
     * @param downServices List of services that are down.
     * @throws IOException if the Discord notification fails.
     */
    private void sendAlertForDownServices(List<String> downServices) throws IOException {
        String alertMessage = "The following services are down: " + String.join(", ", downServices);
        discordService.sendDiscordAlert(alertMessage);
        log.info("Alert sent for down services: {}", downServices);
    }
}
