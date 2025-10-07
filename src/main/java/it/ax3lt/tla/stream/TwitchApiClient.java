package it.ax3lt.tla.stream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TwitchApiClient {

    private TwitchApiClient() {
    }

    public static String fetchToken(String clientId, String clientSecret) throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Client-Id", clientId);
        connection.setDoOutput(true);

        String params = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=client_credentials";
        connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JsonObject json = new Gson().fromJson(response, JsonObject.class);
                return json.get("access_token").getAsString();
            }
        }
        throw new IOException("Failed to retrieve Twitch token, response code: " + responseCode);
    }

    public static String fetchUserId(String twitchUser, String token, String clientId) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/users?login=" + twitchUser);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Client-Id", clientId);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch user data: HTTP error code " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            JsonObject userData = new Gson().fromJson(response, JsonObject.class);
            if (Objects.equals(userData.get("data").toString(), "[]")) {
                return null;
            }
            return userData.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
        }
    }

    public static JsonObject fetchStreamInfo(String userId, String token, String clientId) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/streams?user_id=" + userId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        connection.setRequestProperty("Client-Id", clientId);

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch stream information: HTTP error code " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            return new Gson().fromJson(response, JsonObject.class);
        }
    }
}
