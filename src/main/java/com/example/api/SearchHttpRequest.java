package com.example.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.uri.UriBuilder;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.RemoteException;
import java.util.zip.GZIPInputStream;

@Singleton
public class SearchHttpRequest {
    final ObjectMapper mapper;
    final String micronautApiUrl;

    public SearchHttpRequest(ObjectMapper mapper,@Value("${stackoverflow.api.url}") String micronautApiUrl) {
        this.mapper = mapper;
        this.micronautApiUrl = micronautApiUrl;
    }

    public ApiResponse<Question> execute(String query, String tag, int limit, String sort){
        var client = HttpClient.newHttpClient();

        var uri = UriBuilder.of(micronautApiUrl)
                .path("/search")
                .queryParam("site", "stackoverflow")
                .queryParam("intitle", query)
                .queryParam("tagged", tag)
                .queryParam("pagesize", limit)
                .queryParam("sort", sort)
                .build();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            var input = response.headers().firstValue("Content-Encoding").orElse("").equals("gzip") ?
                    new GZIPInputStream(response.body()) :
                    response.body();

            return mapper.readValue(input, new TypeReference<>(){});

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}