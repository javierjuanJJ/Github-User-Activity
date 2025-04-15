package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static final String URL_API = "https://api.github.com/users/%s/events";

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new Exception("Uso: java GitHubEventsAnalyzer <username>");
            }

            String username = args[0];
            String apiUrl = URL_API.formatted(username);

            String response = makeHttpRequest(apiUrl);
            String parsedJson = parseAndDisplayEvents(response);

            System.out.println(parsedJson);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String makeHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != 200) {
            throw new IOException("Error HTTP: " + connection.getResponseCode());
        }

        String response = readBuffered(connection);
        connection.disconnect();

        return response;
    }

    private static String readBuffered(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        return response.toString();
    }

    private static String parseAndDisplayEvents(String jsonResponse) {
        StringBuilder sb = new StringBuilder();
        JSONArray events = new JSONArray(jsonResponse);

        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            String type = event.getString("type");
            JSONObject repo = event.getJSONObject("repo");
            String repoName = repo.getString("name");

            sb.append("Evento #").append(i + 1).append("\n");
            sb.append("Tipo: ").append(type).append("\n");
            sb.append("Repositorio: ").append(repoName).append("\n");

            if (event.has("payload") && event.getJSONObject("payload").has("commits")) {
                JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");
                sb.append("Número de commits: ").append(commits.length()).append("\n");
            }
            sb.append("----------------------------");

        }
        return sb.toString();
    }
}