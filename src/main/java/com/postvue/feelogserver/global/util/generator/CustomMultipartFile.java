package com.postvue.feelogserver.global.util.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.global.constant.MediaConfigConst;

public class CustomMultipartFile implements MultipartFile {
	private byte[] input;
	private String filename;

	public CustomMultipartFile(byte[] input, String filename) {
		this.input = input;
		this.filename = filename;
	}

	@Override
	public String getName() {
		return null;
	}


	@Override
	public String getOriginalFilename() {
		try {
			// URL 객체를 사용하여 파일명을 추출
			URI uri = new URI(filename);
			String path = uri.getPath();  // 경로 부분만 추출
			String fileName = path.substring(path.lastIndexOf("/") + 1); // 마지막 '/' 이후 부분이 파일명

			// 확장자가 없을 경우 기본값 추가
			if (!fileName.contains(".")) {
				fileName += ".jpg";
			}

			return fileName;
		} catch (URISyntaxException e) {
			throw new RuntimeException("잘못된 URL 형식: " + filename);
		}
	}

	@Override
	public String getContentType() {
		String contentType = FileUtils.getImageMimeTypeFromUrl(filename);

		if (contentType == null || contentType.equals(MediaConfigConst.BASE_FILE_TYPE)) {
			contentType = FileUtils.getMimeTypeFromStream(filename);
		}

		return contentType != null ? contentType : MediaConfigConst.BASE_FILE_TYPE;

		// String originalFilename = getOriginalFilename();
		//
		// // 파일명이 null이면 기본값 반환
		// if (originalFilename == null) {
		// 	return MediaConfigConst.BASE_FILE_TYPE;
		// }
		//
		// // 확장자 추출 로직 개선
		// int lastDotIndex = originalFilename.lastIndexOf(".");
		//
		// if (lastDotIndex == -1 || lastDotIndex >= originalFilename.length() - 1) {
		// 	// 확장자가 없거나 "."이 마지막 문자라면 기본 확장자 적용
		// 	return MediaConfigConst.BASE_FILE_TYPE;
		// }
		//
		// String extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
		// return switch (extension) {
		// 	case "jpg", "jpeg" -> MediaConfigConst.IMAGE_JPEG_TYPE;
		// 	case "png" -> MediaConfigConst.IMAGE_PNG_TYPE;
		// 	case "gif" -> MediaConfigConst.IMAGE_GIF_TYPE;
		// 	case "bmp" -> MediaConfigConst.IMAGE_BMP_TYPE;
		// 	case "webp" -> MediaConfigConst.IMAGE_WEBP_TYPE;
		// 	case "heic" -> MediaConfigConst.IMAGE_HEIC_TYPE;
		// 	default -> "application/octet-stream";
		// };
	}

	@Override
	public boolean isEmpty() {
		return input == null || input.length == 0;
	}

	@Override
	public long getSize() {
		return input.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return input;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(input);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try(FileOutputStream fos = new FileOutputStream(dest)){
			fos.write(input);
		}
	}
}