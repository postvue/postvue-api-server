package com.postvue.feelogserver.app.externallib.ffmpeg;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import com.postvue.feelogserver.global.exception.InternalServerErrorException;

import io.jsonwebtoken.lang.Assert;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FfmpegProcessingService {
	@Value("${external-library.ffmpeg.path}")
	private String ffmpegPath;

	@Value("${external-library.ffprobe.path}")
	private String ffprobePath;

	private FFmpeg ffmpeg;
	private FFprobe ffprobe;

	@PostConstruct
	public void init(){
		try {
			ffmpeg = new FFmpeg(ffmpegPath);
			Assert.isTrue(ffmpeg.isFFmpeg());

			ffprobe = new FFprobe(ffprobePath);
			Assert.isTrue(ffprobe.isFFprobe());

			log.debug("VideoFileUtils init complete.");
		} catch (Exception e) {
			log.error("VideoFileUtils init fail.", e);
		}
	}

	//
	public File convertImageToJpg(File imageInputFile, String imageOutputPath) throws IOException {
		// FFmpeg 명령어로 첫 번째 프레임을 이미지로 저장
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(imageInputFile.getAbsolutePath())  // 입력 비디오 파일
			.addOutput(imageOutputPath)  // 출력 포스터 이미지 파일 경로
			.setFormat("image2")         // 출력 파일 형식
			.addExtraArgs("-vf", "format=jpeg")
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();

		return new File(imageOutputPath);
	}

	/**
	 * Converts the input video file to HLS (m3u8) format.
	 *
	 * @param inputFile  Input video file (e.g., MP4 or MKV)
	 * @param outputPath Directory where the HLS files will be saved
	 */
	public void convertVideo(File inputFile, String outputPath){
		FFmpegBuilder builder = new FFmpegBuilder()
			.overrideOutputFiles(true) // 오버라이드 여부
			.setInput(inputFile.getAbsolutePath()) // 썸네일 생성대상 파일
			.addOutput(outputPath) // 썸네일 파일의 Path
			.setVideoCodec("mp4")
			.setVideoCodec("libx264")
			.setAudioCodec("aac")
			.addExtraArgs("-crf", "25")
			.addExtraArgs("-preset", "slow")
			.addExtraArgs("-vf", "scale='if(gt(iw,ih),1280,-1)':'if(gt(iw,ih),-1,1280)',format=yuv420p") // yuv420p 색상 포맷
			.addExtraArgs("-movflags", "+faststart") // faststart 플래그 설정
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();
	}

	/**
	 * Converts the input video file to HLS (m3u8) format.
	 *
	 * @param videoInputFile Input video file (e.g., MP4 or MKV)
	 * @param outputDirFile Directory where the HLS files will be saved
	 */
	public void convertToHLS(File videoInputFile, File outputDirFile, String m3u8FileName) {
		try {
			if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
				throw new IOException("Failed to create output directory: " + outputDirFile.getAbsolutePath());
			}

			String outputM3u8 = new File(outputDirFile, m3u8FileName).getAbsolutePath();

			FFmpegBuilder builder = new FFmpegBuilder()
				.overrideOutputFiles(true) // Allow overwriting existing files
				.setInput(videoInputFile.getAbsolutePath()) // Input file
				.addOutput(outputM3u8) // Output playlist file
				.addExtraArgs("-codec:v", "libx264") // H.264 video codec
				.addExtraArgs("-codec:a", "aac") // AAC audio codec
				.addExtraArgs("-ac", "2") // Stereo audio
				.addExtraArgs("-maxrate", "1000k") // Maximum bitrate: 1000k
				.addExtraArgs("-bufsize", "2000k") // Buffer size for smoother encoding
				.addExtraArgs("-hls_time", "10") // Segment duration (10 seconds)
				.addExtraArgs("-hls_list_size", "0") // Include all segments in the playlist
				.addExtraArgs("-crf", "22")
				.addExtraArgs("-preset", "veryfast")
				.addExtraArgs("-vf",
					"zscale=t=linear:npl=100," + // Tone mapping for HDR to SDR conversion
					"format=gbrpf32le," + // Set a high precision format
					"zscale=p=bt709," + // Convert to BT.709 color primaries
					"tonemap=tonemap=hable:desat=0," + // Apply Hable tone-mapping
					"zscale=t=bt709:m=bt709:r=tv," + // Convert color range to BT.709 TV range
					"format=yuv420p," + // Final format for compatibility with most players
					"scale='if(gt(iw,ih),1280,-1)':'if(gt(iw,ih),-1,1280)'") // Resize to 1280 while maintaining aspect ratio
				.addExtraArgs("-movflags", "+faststart") // faststart 플래그 설정
				.addExtraArgs("-colorspace", "bt709") // 색상 공간 설정
				.addExtraArgs("-color_trc", "bt709") // 전달 함수 설정 (SDR)
				.addExtraArgs("-color_primaries", "bt709") // 색상 원색 설정
				.addExtraArgs("-hls_segment_filename", new File(outputDirFile, "segment_%03d.ts").getAbsolutePath()) // Segment naming pattern
				.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(builder).run();

			log.info("Video successfully converted to HLS format. Playlist: {}", outputM3u8);
		} catch (Exception e) {
			log.error("Failed to convert video to HLS format.", e);
			throw new InternalServerErrorException("비디오 처리에 오류가 발생했습니다.");
		}
	}

	/**
	 * Converts the input video file to export Poster Image
	 *
	 * @param videoInputFile Input video file (e.g., MP4 or MKV)
	 * @param outputPosterImagePath Directory where the JPG files will be saved
	 */
	public File generateVideoPoster(File videoInputFile, String outputPosterImagePath) throws IOException {
		// FFmpeg 명령어로 첫 번째 프레임을 이미지로 저장
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(videoInputFile.getAbsolutePath())  // 입력 비디오 파일
			.addOutput(outputPosterImagePath)  // 출력 포스터 이미지 파일 경로
			.setFrames(1)  // 첫 번째 프레임만 추출
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();

		return new File(outputPosterImagePath);
	}
}


