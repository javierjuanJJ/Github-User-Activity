package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java GitHubEventsAnalyzer <username>");
            return;
        }

        String username = args[0];
        String apiUrl = "https://api.github.com/users/" + username + "/events";

        try {
            String response = makeHttpRequest(apiUrl);
            parseAndDisplayEvents(response);
        } catch (IOException e) {
            System.out.println("Error al obtener los eventos: " + e.getMessage());
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

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    private static void parseAndDisplayEvents(String jsonResponse) {
        JSONArray events = new JSONArray(jsonResponse);

        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            String type = event.getString("type");
            JSONObject repo = event.getJSONObject("repo");
            String repoName = repo.getString("name");

            System.out.println("Evento #" + (i+1));
            System.out.println("Tipo: " + type);
            System.out.println("Repositorio: " + repoName);

            if (event.has("payload") && event.getJSONObject("payload").has("commits")) {
                JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");
                System.out.println("Número de commits: " + commits.length());
            }

            System.out.println("----------------------------");
        }
    }
}