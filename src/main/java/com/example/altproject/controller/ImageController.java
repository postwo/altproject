package com.example.altproject.controller;

import com.example.altproject.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    //게시글 에 있는 이미지와 프로필 이미지 저장
    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file
    ) {
        String url = imageService.upload(file);
        return url;
    }

    //이미지 불러오기
    @GetMapping(value = "{fileName}",produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public Resource getImage(
            @PathVariable("fileName") String fileName
    ){
        Resource resource = imageService.getImage(fileName);
        return resource;
    }

    @DeleteMapping("/{fileName}")
    public void deleteFile(
            @PathVariable("fileName") String fileName
    ) {
         imageService.delete(fileName);
    }

}
