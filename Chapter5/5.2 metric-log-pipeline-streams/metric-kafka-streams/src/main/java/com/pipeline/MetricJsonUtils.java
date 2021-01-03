package com.pipeline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MetricJsonUtils {

    public static double getTotalCpuPercent(String value) {
        return new JsonParser().parse(value).getAsJsonObject().get("system").getAsJsonObject().get("cpu")
                .getAsJsonObject().get("total").getAsJsonObject().get("norm").getAsJsonObject().get("pct").getAsDouble();
    }

    public static String getMetricName(String value) {
        return new JsonParser().parse(value).getAsJsonObject().get("metricset").getAsJsonObject().get("name")
                .getAsString();
    }

    public static String  getHostTimestamp(String value) {
        JsonObject objectValue = new JsonParser().parse(value).getAsJsonObject();
        JsonObject result = objectValue.getAsJsonObject("host");
        result.add("timestamp", objectValue.get("@timestamp"));
        return result.toString();
    }
}
