package dev.klepto.bunnytracker.record.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class ApiJsonProvider implements RecordsJsonProvider {

    private final HttpClient httpClient;
    private final HttpRequest httpRequest;

    public ApiJsonProvider(String apiUrl) {
        this.httpClient = HttpClient.newBuilder().build();
        this.httpRequest = HttpRequest.newBuilder().uri(URI.create(apiUrl)).timeout(Duration.ofMinutes(1)).build();
    }

    @Override
    public String fetchRecordsJson() throws IOException, InterruptedException {
        return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
    }

}
