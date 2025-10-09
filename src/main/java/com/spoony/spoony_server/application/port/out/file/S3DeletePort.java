package com.spoony.spoony_server.application.port.out.file;

import java.util.List;

public interface S3DeletePort {
	void deleteImagesFromS3(List<String> imageUrls);
}
