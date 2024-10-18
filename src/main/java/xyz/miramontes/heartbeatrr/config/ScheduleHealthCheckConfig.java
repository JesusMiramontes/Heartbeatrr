package xyz.miramontes.heartbeatrr.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.miramontes.heartbeatrr.service.HealthCheckService;

/**
 * Configures a scheduled task to periodically check the health of services. The delay between
 * checks is configurable via application properties.
 */
@Component
@Slf4j
public class ScheduleHealthCheckConfig {

    @Value("${delay.between.checks}") // Delay between checks is injected from
    // application.properties
    private long delayBetweenChecks;

    private final HealthCheckService healthCheckService;

    public ScheduleHealthCheckConfig(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    /**
     * Scheduled task to check the health of services. The fixed delay is configurable via the
     * application properties.
     */
    @Scheduled(fixedDelayString = "${delay.between.checks}")
    private void checkAllServicesOnSchedule() {
        log.info("Starting job on schedule");
        healthCheckService.checkAllServices();
        log.info("Job finished");
    }
}
