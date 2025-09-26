package com.example.altproject.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String upload(MultipartFile file) ;
    Resource getImage(String fileName);
    boolean delete(String fileName);
}