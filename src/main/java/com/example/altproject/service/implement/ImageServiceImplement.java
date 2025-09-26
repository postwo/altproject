package com.example.altproject.service.implement;

import com.example.altproject.common.ErrorStatus;
import com.example.altproject.common.exception.ApiException;
import com.example.altproject.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImplement implements ImageService {

    @Value("${spring.file.path}")
    private String filePath;

    @Value("${spring.file.url}")
    private String fileUrl;

    // 1. 파일 업로드
    @Override
    @Transactional
    // throws IOException 제거: 내부에서 catch 후 런타임 예외로 변환합니다.
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(ErrorStatus.EMPTY_FILE, "업로드할 파일이 비어있습니다.");
        }

        String originFileName = file.getOriginalFilename();

        String extension = "";
        if (originFileName != null && originFileName.lastIndexOf(".") != -1) {
            extension = originFileName.substring(originFileName.lastIndexOf("."));
        }

        String uuid = UUID.randomUUID().toString();
        String saveFileName = uuid + extension;
        String savePath = filePath + saveFileName;

        try {
            file.transferTo(new File(savePath));
        } catch (IOException e) {
            throw new ApiException(ErrorStatus.FILE_UPLOAD_FAILED, e, "파일 저장 중 I/O 오류가 발생했습니다.");
        }

        return fileUrl + saveFileName;
    }

    @Override
    public Resource getImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new ApiException(ErrorStatus.INVALID_FILE_NAME, "유효하지 않은 파일 이름입니다.");
        }

        Resource resource;
        try {
            // UrlResource 생성자(체크 예외)를 try-catch로 묶습니다.
            resource = new UrlResource("file:" + filePath + fileName);
        } catch (MalformedURLException e) {
            throw new ApiException(ErrorStatus.INVALID_FILE_PATH, e, "파일 경로 생성 중 오류가 발생했습니다.");
        }

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new ApiException(ErrorStatus.NOT_EXISTED_FILE, "해당 파일이 존재하지 않거나 읽을 수 없습니다. File: " + fileName);
        }
    }

    @Override
    @Transactional
    public boolean delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new ApiException(ErrorStatus.INVALID_FILE_NAME, "삭제할 파일 이름이 지정되지 않았습니다.");
        }

        File fileToDelete = new File(filePath + fileName);

        if (!fileToDelete.exists()) {
            throw new ApiException(ErrorStatus.NOT_EXISTED_FILE, "삭제할 파일이 로컬 경로에 존재하지 않습니다.");
        }

        if (fileToDelete.delete()) {
            return true; // 삭제 성공
        } else {
            throw new ApiException(ErrorStatus.FILE_DELETE_FAILED, "파일 삭제 권한이 없거나 삭제 중 오류가 발생했습니다.");
        }
    }
}