package xyz.miramontes.heartbeatrr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Service responsible for sending alerts to a Discord webhook. */
@Service
public class DiscordService {

    private final ObjectMapper objectMapper;
    private final String discordWebhook;

    /**
     * Constructs the DiscordService with required dependencies.
     *
     * @param discordWebhook the Discord webhook URL (injected from application properties).
     * @param objectMapper the ObjectMapper for converting Java objects to JSON.
     */
    public DiscordService(
            @Value("${heartbeatrr.discord.service.webhook}") String discordWebhook,
            ObjectMapper objectMapper) {
        this.discordWebhook = discordWebhook;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a message to a configured Discord webhook.
     *
     * @param message The message content to send.
     * @throws IOException if there's a failure in sending the message.
     */
    public void sendDiscordAlert(String message) throws IOException {
        // Build the message payload as a JSON object
        Map<String, String> payload = Collections.singletonMap("content", message);
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // Send the POST request to the Discord webhook
        sendPostRequest(jsonPayload);
    }

    /**
     * Sends a POST request to the Discord webhook with the provided JSON payload.
     *
     * @param jsonPayload The JSON-formatted string to send in the request body.
     * @throws IOException if there's an error during the connection or data transmission.
     */
    private void sendPostRequest(String jsonPayload) throws IOException {
        URL url = createURL(discordWebhook);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Write the JSON payload to the connection output stream
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            // Ensure that the response input stream is closed
            connection.getInputStream().close();
        } finally {
            // Always disconnect to free resources
            connection.disconnect();
        }
    }

    /**
     * Creates a URL object from the webhook string using URI to handle deprecation in Java 20.
     *
     * @param webhookUrl The webhook URL as a string.
     * @return The URL object.
     * @throws MalformedURLException if the URI cannot be converted to a valid URL.
     */
    private URL createURL(String webhookUrl) throws MalformedURLException {
        try {
            return URI.create(webhookUrl).toURL();
        } catch (IllegalArgumentException e) {
            throw new MalformedURLException("Invalid URL: " + webhookUrl);
        }
    }
}
