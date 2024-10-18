package xyz.miramontes.heartbeatrr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuration class for setting up a customizable {@link RetryTemplate}. Allows dynamic
 * configuration of retry attempts and backoff delay through application properties.
 */
@Configuration
public class RetryConfig {

    /**
     * Creates a {@link RetryTemplate} bean to be used for retryable operations.
     *
     * @param maxAttempts the maximum number of retry attempts
     * @param backoffDelay the delay between retries (in milliseconds)
     * @return a fully configured {@link RetryTemplate} instance
     */
    @Bean
    public RetryTemplate retryTemplate(
            @Value("${retry.max.attempts}") int maxAttempts,
            @Value("${retry.backoff.delay}") long backoffDelay) {

        return configureRetryTemplate(maxAttempts, backoffDelay);
    }

    /**
     * Configures the retry policy and backoff policy for the {@link RetryTemplate}.
     *
     * @param maxAttempts the maximum number of retry attempts
     * @param backoffDelay the delay between retries (in milliseconds)
     * @return a configured {@link RetryTemplate}
     */
    private RetryTemplate configureRetryTemplate(int maxAttempts, long backoffDelay) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(createRetryPolicy(maxAttempts));
        retryTemplate.setBackOffPolicy(createBackOffPolicy(backoffDelay));
        return retryTemplate;
    }

    /**
     * Creates a {@link SimpleRetryPolicy} with a configurable number of maximum retry attempts.
     *
     * @param maxAttempts the maximum number of retry attempts
     * @return a configured {@link SimpleRetryPolicy}
     */
    private SimpleRetryPolicy createRetryPolicy(int maxAttempts) {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        return retryPolicy;
    }

    /**
     * Creates a {@link FixedBackOffPolicy} with a configurable backoff delay between retries.
     *
     * @param backoffDelay the delay between retries (in milliseconds)
     * @return a configured {@link FixedBackOffPolicy}
     */
    private FixedBackOffPolicy createBackOffPolicy(long backoffDelay) {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backoffDelay);
        return backOffPolicy;
    }
}
