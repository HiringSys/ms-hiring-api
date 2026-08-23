package com.example.hiringsys.service;

public interface StorageService {
    void upload(String path, byte[] content, String contentType);
    byte[] download(String path);
    void delete(String path);
}
