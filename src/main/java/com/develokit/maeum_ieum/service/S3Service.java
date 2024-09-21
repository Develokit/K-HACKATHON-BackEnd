package com.develokit.maeum_ieum.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.develokit.maeum_ieum.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client S3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final Logger log = LoggerFactory.getLogger(S3Service.class);


    public String uploadImage(MultipartFile file){
        String keyName = "uploads/"+file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try{
            S3Client.putObject(bucket, keyName, file.getInputStream(), metadata);
        }catch(IOException e){
            e.getMessage();
        }
        String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucket, keyName);
        return fileUrl;
    }
    public void deleteImage(@RequestBody String imgPath) throws MalformedURLException {
        String objectKey = "";
        try {
            URL url = new URL(imgPath);
            String path = url.getPath();
            // URL의 첫 번째 '/' 문자를 제거하고 반환
            objectKey = path.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("이미지 삭제 시도 실패: 이미지 URL 파싱 과정에서 오류 발생");
        }
        try {
            S3Client.deleteObject(bucket, objectKey);
            log.debug("디버그 : 버킷에서 이미지 삭제 완료");
        } catch (AmazonServiceException e) {
            log.error("버킷에서 이미지 삭제 중 오류 발생");
            throw new CustomApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR);
            //System.exit(1);
        }
    }
}
