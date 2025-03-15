package com.postvue.feelogserver.global.util.generator;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.postvue.feelogserver.global.constant.MediaConfigConst;
import com.postvue.feelogserver.global.exception.BadRequestErrorException;


public final class FileUtils {
	// 이미지 URL을 받아 File로 저장하는 메서드
	public static File downloadImageAsFile(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");

			// 임시 파일 생성 (자동 삭제 옵션 없음)
			File tempFile = Files.createTempFile(MediaConfigConst.UPLOAD_TEMP_FILE_PREFIX_NAME, MediaConfigConst.IMAGE_JPEG_FORMAT).toFile();
			tempFile.deleteOnExit(); // JVM 종료 시 자동 삭제

			try (InputStream inputStream = connection.getInputStream();
				 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			return tempFile;
		}
		catch (IOException e){
			throw new BadRequestErrorException("잘못된 Url입니다.");
		}
	}
	public static MultipartFile convertUrlToMultipartFile(String imageUrl)  {
		try {
			URL url = new URL(imageUrl);

			try (InputStream inputStream = url.openStream();
				 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				// 1) image url -> byte[]
				BufferedImage urlImage = ImageIO.read(inputStream);
				ImageIO.write(urlImage, MediaConfigConst.IMAGE_JPEG, bos);
				byte[] byteArray = bos.toByteArray();
				// 2) byte[] -> MultipartFile
				return new CustomMultipartFile(byteArray, imageUrl);
			}
			catch (Exception e){
				return null;
			}

		}
		catch (IOException e){
			throw new BadRequestErrorException("잘못된 url 입니다.");
		}
	}

	public static String getMimeTypeFromStream(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			URLConnection connection = url.openConnection();

			try (InputStream inputStream = connection.getInputStream()) {
				return URLConnection.guessContentTypeFromStream(inputStream);
			}
		} catch (IOException e) {
			return "application/octet-stream";
		}
	}

	public static String getImageMimeTypeFromUrl(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD"); // 이미지 전체 다운로드 없이 Content-Type 확인
			connection.connect();

			String contentType = connection.getContentType();
			connection.disconnect();

			return contentType;
		} catch (IOException e) {
			return "application/octet-stream"; // 기본값 반환
		}
	}

	public static String getExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
			return fileName.substring(lastIndex + 1);
		}
		else{
			throw new BadRequestErrorException("파일 형식이 아닙니다.");
		}
	}

	public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		return new CustomMultipartFile(fileBytes, file.getName());
	}

}
