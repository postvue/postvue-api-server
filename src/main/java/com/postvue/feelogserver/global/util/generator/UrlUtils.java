package com.postvue.feelogserver.global.util.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

public class UrlUtils {
	public static String convertToDataURL(String urlString) {
		try {
			// Step 1: Fetch the content from the URL
			URL url = new URL(urlString);
			InputStream inputStream = url.openStream();

			// Step 2: Convert the content to a byte array
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			byte[] bytes = byteArrayOutputStream.toByteArray();

			// Step 3: Encode the byte array to a Base64 string
			String base64 = Base64.getEncoder().encodeToString(bytes);

			// Step 4: Determine the MIME type (you might need to improve this)
			String mimeType = url.openConnection().getContentType();

			// Step 5: Construct the Data URL
			return "data:" + mimeType + ";base64," + base64;
		} catch (IOException ex) {
			return urlString;
		}
	}

	public static String getWebsocketTargetUri(String destination, Long targetId) {
		return destination + "/" + targetId;
	}
}
