package com.fares7elsadek.syncspace.shared.api;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    public String upload(MultipartFile file) throws IOException;
    public void remove(String fileName) throws IOException;
}
