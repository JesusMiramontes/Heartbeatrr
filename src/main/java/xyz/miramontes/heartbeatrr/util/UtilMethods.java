package xyz.miramontes.heartbeatrr.util;

import java.util.Map;
import java.util.stream.Collectors;

public class UtilMethods {
    private UtilMethods() {}

    public static String reduceSeconds(int seconds) {
        StringBuilder output = new StringBuilder();

        int days = seconds / (24 * 60 * 60);
        seconds %= 24 * 60 * 60;
        if (days > 0) output.append(days).append(days > 1 ? " days " : " day ");

        int hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        if (hours > 0) output.append(hours).append(hours > 1 ? " hours " : " hour ");

        int minutes = seconds / 60;
        seconds %= 60;
        if (minutes > 0) output.append(minutes).append(minutes > 1 ? " minutes " : " minute ");

        if (seconds > 0) output.append(seconds).append(seconds > 1 ? " seconds" : " second");

        return output.toString();
    }

    public static String convertMapToString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue() + ")")
                .collect(Collectors.joining(", "));
    }
}
