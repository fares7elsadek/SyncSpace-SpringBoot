package com.fares7elsadek.syncspace.shared.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public String upload(MultipartFile file);
}
