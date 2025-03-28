package com.spoony.spoony_server.adapter.out.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spoony.spoony_server.application.port.out.post.PostCreatePort;
import com.spoony.spoony_server.application.port.out.post.PostDeletePort;
import com.spoony.spoony_server.global.annotation.Adapter;
import com.spoony.spoony_server.global.exception.BusinessException;
import com.spoony.spoony_server.global.message.business.S3ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Adapter
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3Adapter implements PostCreatePort, PostDeletePort {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String PROFILE_IMG_DIR = "profile";
    private String POST_IMG_DIR = "post";

    public String saveProfileImage(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new BusinessException(S3ErrorMessage.FILE_CHANGE_FAIL));
        return upload(uploadFile, PROFILE_IMG_DIR);
    }

    public String savePostImage(MultipartFile multipartFile) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new BusinessException(S3ErrorMessage.FILE_CHANGE_FAIL));
        return upload(uploadFile, POST_IMG_DIR);
    }

    public List<String> savePostImages(List<MultipartFile> photos) throws IOException {
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile photo : photos) {
            String photoUrl = savePostImage(photo); // 개별 파일 업로드
            photoUrls.add(photoUrl);
        }
        return photoUrls;
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        String photosDir = "src/main/resources/photos/";

        File directory = new File(photosDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File convertFile = new File(photosDir + file.getOriginalFilename());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public void createDir(String bucketName, String folderName) {
        amazonS3Client.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    public void deleteImagesFromS3(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            String key = extractKeyFromUrl(imageUrl);
            log.info("Deleting image from S3 with key: {}", key);
            amazonS3Client.deleteObject(bucket, key);
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        int index = imageUrl.indexOf(".com/");
        if (index == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format: " + imageUrl);
        }
        return imageUrl.substring(index + 5);
    }
}
