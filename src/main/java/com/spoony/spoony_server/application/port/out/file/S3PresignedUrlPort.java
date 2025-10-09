package com.spoony.spoony_server.application.port.out.file;

import java.net.URL;

public interface S3PresignedUrlPort {
	URL generatePresignedPutUrl(String key, String contentType);
}
