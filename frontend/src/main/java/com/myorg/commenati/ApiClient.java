package com.myorg.commentai;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private final HttpClient client;
    private final Gson gson;
    private final String apiUrl;

    public ApiClient(String apiUrl) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.gson = new Gson();
        this.apiUrl = apiUrl;
    }

    public AnalysisResult analyzeText(String text) throws Exception {
        JsonObject payload = new JsonObject();
        payload.addProperty("text", text);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/analyze"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        String sentiment = json.get("sentiment").getAsString();
        String generated = json.get("response").getAsString();
        return new AnalysisResult(sentiment, generated);
    }

    public static class AnalysisResult {
        public final String sentiment;
        public final String response;

        public AnalysisResult(String sentiment, String response) {
            this.sentiment = sentiment;
            this.response = response;
        }
    }
}
