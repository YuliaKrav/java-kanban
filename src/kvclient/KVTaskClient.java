package kvclient;

import exception.KVClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private static final int HTTP_OK = 200;
    private final String serverUrl;
    HttpClient client;
    private String apiToken;

    public KVTaskClient(String serverUrl) throws KVClientException {
        this.serverUrl = serverUrl;
        this.client = HttpClient.newHttpClient();
        try {
            this.apiToken = register();
        } catch (Exception ex) {
            throw new KVClientException("KVClient registration error", ex);
        }
    }

    private String register() throws KVClientException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + "/register"))
                    .GET()
                    .build();
        } catch (URISyntaxException ex) {
            throw new KVClientException("Invalid URI", ex);
        }
        return getResponseBody(request);
    }

    public void put(String key, String json) throws KVClientException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
        } catch (URISyntaxException ex) {
            throw new KVClientException("Invalid URI", ex);
        }
        getResponseBody(request);
    }

    public String load(String key) throws KVClientException {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
        } catch (URISyntaxException ex) {
            throw new KVClientException("Invalid URI", ex);
        }
        return getResponseBody(request);
    }

    private String getResponseBody(HttpRequest request) throws KVClientException {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            throw new KVClientException("Error sending request", ex);
        }
        checkResponse(response);
        return response.body();
    }

    private void checkResponse(HttpResponse<String> response) throws KVClientException {
        if (response.statusCode() != HTTP_OK) {
            throw new KVClientException("Server returned status code: " + response.statusCode());
        }
    }
}
