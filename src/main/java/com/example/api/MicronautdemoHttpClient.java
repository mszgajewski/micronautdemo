package com.example.api;

import io.micronaut.http.client.annotation.Client;

@Client("${stackoverflow.api.url}")
public interface MicronautdemoHttpClient {

    ApiResponse<Question> search();
}
