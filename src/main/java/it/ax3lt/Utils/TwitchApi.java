package it.ax3lt.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;

public class TwitchApi {
    public static String getToken(String client_id, String client_secret) throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Client-Id", client_id);
        con.setDoOutput(true);

        String params = "client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type=client_credentials";
        con.getOutputStream().write(params.getBytes());

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = in.lines().collect(Collectors.joining());
            in.close();

            JsonObject json = new Gson().fromJson(response, JsonObject.class);
            return json.get("access_token").getAsString();
        } else {
            throw new IOException("Failed to get token, response code: " + responseCode);
        }
    }


    public static String getUserId(String twitchUser, String token, String clientId) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/users?login=" + twitchUser);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Client-Id", clientId);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to get user data: HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(response.toString(), JsonObject.class);

        if(Objects.equals(userData.get("data").toString(), "[]"))
            return null;
        return userData.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
    }


    public static JsonObject getStreamInfo(String userId, String token, String clientId) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/streams?user_id=" + userId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Client-Id", clientId);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to get stream information: HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new Gson().fromJson(response.toString(), JsonObject.class);
//        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }
}
