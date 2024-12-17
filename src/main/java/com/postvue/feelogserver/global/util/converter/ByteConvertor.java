package com.postvue.feelogserver.global.util.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteConvertor {
	public static File convertByteArrayToFile(byte[] data, String filePath) throws IOException {
		// 파일 경로에 새로운 파일을 생성
		File file = new File(filePath);

		// 파일 출력 스트림을 생성하여 byte[] 데이터를 파일에 씁니다.
		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(data);  // byte[] 데이터를 파일에 씁니다.
			fos.flush();  // 데이터를 강제로 플러시하여 파일에 저장
		}
		return file;
	}
}
