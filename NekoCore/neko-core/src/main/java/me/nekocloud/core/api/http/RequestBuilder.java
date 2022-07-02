package me.nekocloud.core.api.http;

import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBuilder {

    private final String urlString;

    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    private String requestMethod = "GET";

    private final boolean hasParams;

    public RequestBuilder(final @NotNull String urlString) {
        this.urlString = urlString;
        this.hasParams = urlString.contains("?");
    }

    public RequestBuilder parameter(final @NotNull String key, @NotNull String value) {
        parameters.put(key, value);
        return this;
    }

    public RequestBuilder header(final @NotNull String key, @NotNull String value) {
        headers.put(key, value);
        return this;
    }

    public RequestBuilder method(final @NotNull String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    private URL buildUrl() throws MalformedURLException {
        if (parameters.isEmpty()) {
            return new URL(urlString);
        }

        return new URL(urlString
                .concat(hasParams ? "" : "?")
                .concat(parameters.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + (entry.getValue() != null ? escape(entry.getValue()) : ""))
                        .collect(Collectors.joining("&"))));
    }

    public String makeRequest() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) buildUrl().openConnection();

            //добавляем хеадеры
            connection.setRequestProperty("User-Agent", "NekoCore");
            connection.setRequestProperty("Content-Type", "application/json");
            headers.forEach(connection::setRequestProperty);

            connection.setRequestMethod(requestMethod);
            connection.setReadTimeout(2_000);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                return content.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String escape(String data) {
        return URLEncoder.encode(data, StandardCharsets.UTF_8);
    }
}
