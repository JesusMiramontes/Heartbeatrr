package xyz.miramontes.heartbeatrr.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.miramontes.heartbeatrr.service.HealthCheckService;
import xyz.miramontes.heartbeatrr.util.UtilMethods;

/**
 * A configuration class responsible for scheduling periodic health checks of various services. The
 * delay between each health check is configurable via application properties.
 */
@Component
@Slf4j
public class ScheduleHealthCheckConfig {

    /**
     * The delay between scheduled health checks in seconds, configurable via application
     * properties.
     */
    @Value("${heartbeatrr.healthcheck.schedule.delay}")
    private int scheduleDelaySeconds;

    private final HealthCheckService healthCheckService;

    /**
     * Cached text that represents the next heartbeat interval in a human-readable format. This is
     * calculated once and reused for logging, as the interval doesn't change during runtime.
     */
    private String nextHeartbeatText;

    /**
     * Constructor for {@link ScheduleHealthCheckConfig}.
     *
     * @param healthCheckService The service that performs health checks on configured services.
     */
    public ScheduleHealthCheckConfig(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    /**
     * Initializes the {@code nextHeartbeatText} once after the bean construction. This converts the
     * configured delay from seconds to a human-readable format.
     */
    @PostConstruct
    private void initNextHeartbeatText() {
        nextHeartbeatText = UtilMethods.reduceSeconds(scheduleDelaySeconds);
        log.info("Health check scheduled every: {}", nextHeartbeatText);
    }

    /**
     * Periodically checks the health of the services. This task is scheduled with a fixed delay
     * between executions, based on the configured delay in seconds.
     *
     * @throws IOException If there is an issue while checking the service health or sending alerts.
     */
    @Scheduled(fixedDelayString = "#{${heartbeatrr.healthcheck.schedule.delay} * 1000}")
    private void checkAllServicesOnSchedule() throws IOException {
        log.debug("Starting health check job.");
        healthCheckService.checkAllServices();
        log.info("Health check completed. Next check in: {}", nextHeartbeatText);
    }
}
