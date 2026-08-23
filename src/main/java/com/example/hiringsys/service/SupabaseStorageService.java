package com.example.hiringsys.service;

import com.example.hiringsys.exception.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class SupabaseStorageService implements StorageService {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final String supabaseUrl;
    private final String secretKey;
    private final String bucket;

    public SupabaseStorageService(
            @Value("${app.supabase.url}") String supabaseUrl,
            @Value("${app.supabase.secret-key}") String secretKey,
            @Value("${app.supabase.bucket}") String bucket
    ) {
        this.supabaseUrl = supabaseUrl.replaceAll("/+$", "");
        this.secretKey = secretKey;
        this.bucket = bucket;
    }

    @Override
    public void upload(String path, byte[] content, String contentType) {
        HttpRequest request = baseRequest(path)
                .header("Content-Type", contentType)
                .header("x-upsert", "false")
                .POST(HttpRequest.BodyPublishers.ofByteArray(content))
                .build();
        sendWithoutBody(request, "enviar o arquivo");
    }

    @Override
    public byte[] download(String path) {
        HttpRequest request = baseRequest(path).GET().build();
        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new StorageException("O Supabase recusou o download (HTTP " + response.statusCode() + ")");
            }
            return response.body();
        } catch (IOException exception) {
            throw new StorageException("Falha de comunicação ao baixar o arquivo", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new StorageException("Download do arquivo interrompido", exception);
        }
    }

    @Override
    public void delete(String path) {
        HttpRequest request = baseRequest(path).DELETE().build();
        sendWithoutBody(request, "excluir o arquivo");
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder(objectUri(path))
                .timeout(Duration.ofSeconds(30))
                .header("apikey", secretKey)
                .header("Authorization", "Bearer " + secretKey);
    }

    private URI objectUri(String path) {
        return URI.create(supabaseUrl + "/storage/v1/object/" + encode(bucket) + "/" +
                Arrays.stream(path.split("/"))
                        .map(this::encode)
                        .collect(Collectors.joining("/")));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private void sendWithoutBody(HttpRequest request, String operation) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new StorageException("Não foi possível " + operation + " no Supabase (HTTP " +
                        response.statusCode() + ")");
            }
        } catch (IOException exception) {
            throw new StorageException("Falha de comunicação ao " + operation, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new StorageException("Operação de storage interrompida ao " + operation, exception);
        }
    }
}
