# Heartbeatrr Project - README

## Project Overview

Heartbeatrr is an easy-to-use service monitoring tool that helps you keep track of the health of your online services. Imagine you have a few websites, applications, or online services that you rely on, and you want to know if any of them go offline or experience issues. Heartbeatrr checks these services regularly and lets you know if something goes wrong.

### Here’s how it works:

1.	Service Health Monitoring: Heartbeatrr periodically checks the status of different services by “pinging” their URLs to see if they are responding. These services could be websites, APIs, or any other online system that provides a URL for checking availability.
2.	Automatic Alerts: If any of the services fail to respond or return an error, Heartbeatrr immediately sends a notification to your Discord channel. This way, you are promptly informed if something goes wrong without having to manually check everything.
3.	Configurable Timing: You can decide how often you want Heartbeatrr to check the services. For example, it could check them every 30 minutes, every hour, or at any interval that fits your needs. This is done through simple settings that are easy to adjust.
4.	Retry on Failure: Sometimes, services might be temporarily unavailable due to brief issues. Instead of notifying you immediately for every small hiccup, Heartbeatrr can retry the failed services a few times before sending an alert, just to make sure it’s not a short-term glitch.
5.	Customizable for Your Needs: You can easily tell Heartbeatrr which services to monitor by providing their URLs. It’s as simple as giving the app a list of web addresses, and it takes care of the rest.

## Who is it for?

Heartbeatrr is ideal for anyone who manages online services and wants to make sure everything is running smoothly without constantly checking manually. Whether you’re managing a few websites, APIs, or internal tools, Heartbeatrr gives you peace of mind by automatically checking the status of these services and notifying you of any issues.

This app is also great for those who use platforms like Discord for team communication. Instead of getting notifications via email or other channels, you’ll receive real-time alerts directly in your Discord chat, where you can quickly take action.

This simple yet powerful tool automates the health-checking process and keeps you informed, making it an essential part of your service management toolkit.

## Features
	- Periodic health checks for multiple services.
	- Configurable retries for failed health checks.
	- Sends alerts via Discord webhook when a service goes down.
	- Easily deployable using Docker.

## Docker Configuration
The project can be easily deployed using Docker. You need to configure the Docker environment variables to suit your needs. Here’s a breakdown of the variables you need to configure.

### Environment Variables

	- HEARTBEATRR_CONNECTION_TIMEOUT: Sets the HTTP connection timeout (in milliseconds). This determines how long the system waits for a service to respond before timing out. Example: 3000 (3 seconds).
	- HEARTBEATRR_RETRY_BACKOFF_DELAY: The delay (in milliseconds) between retry attempts when a health check fails. Example: 1000 (1 second).
	- HEARTBEATRR_RETRY_MAX_ATTEMPTS: Sets the maximum number of retry attempts for failed health checks. Example: 3.
	- HEARTBEATRR_HEALTHCHECK_SCHEDULE_DELAY: Delay between scheduled health checks (in seconds). This is how often the system will check the health of services. Example: 1800 (30 minutes).
	- HEARTBEATRR_SERVICES_URLS: A JSON-like object specifying the services to be checked and their URLs. For example: {"Google":"http://google.com", "Yahoo":"http://yahoo.com"}.
	- HEARTBEATRR_DISCORD_SERVICE_WEBHOOK: The Discord webhook URL to send notifications when services are down. This is required if you want to receive alerts.

## Steps to Run with Docker
1. Configure the docker-compose.yml file with the required services, retries, and Discord webhook.
2. Run the application with Docker Compose:
```bash
docker-compose up -d
```
3. The application will now start running and periodically check the services you’ve configured. If any service is down, you’ll receive a Discord notification.


## For Developers
This section is for developers who want to modify the code, run tests, or contribute to the project.

### Project Structure

The project follows a standard Spring Boot structure:

	• src/main/java: Contains the application source code.
	• src/main/resources: Contains application configuration files.
	• src/test/java: Contains unit and integration tests.

### Building the Project
Make sure you have Maven installed. To build the project:

### Contributing

We welcome contributions to the project! Here’s how you can contribute:

	1. Fork the repository.
	2. Create a new branch with a descriptive name (git checkout -b feature-branch-name).
	3. Make your changes and test them.
	4. Push your changes to the branch (git push origin feature-branch-name).
	5. Open a Pull Request and describe your changes.

Be sure to follow our coding standards and add proper documentation for any new features.

That’s it! If you have any questions, feel free to open an issue or reach out for support. Happy monitoring!