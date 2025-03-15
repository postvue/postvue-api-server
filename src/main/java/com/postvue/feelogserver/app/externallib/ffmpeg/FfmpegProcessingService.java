package com.postvue.feelogserver.app.externallib.ffmpeg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

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

	@Value("${external-library.ffmpeg.videoCodec}")
	private String videoCodec;

	@Value("${external-library.ffmpeg.useCuda}")
	private Boolean useCuda;

	@Value("${external-library.ffmpeg.preset}")
	private String preset;

	@Value("${external-library.ffmpeg.maxRate}")
	private int maxRate;

	@Value("${external-library.ffmpeg.bufSize}")
	private int bufSize;



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
	public void convertToHLS(File videoInputFile, File outputDirFile, String m3u8FileName) throws IOException {
		try {
			if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
				throw new IOException("Failed to create output directory: " + outputDirFile.getAbsolutePath());
			}

			// Initialize FFprobe to get original bitrate
			double originalBitrate = (double) ffprobe.probe(videoInputFile.getAbsolutePath()).getFormat().bit_rate / 1000;

			// Calculate MAXRATE and BUFSIZE
			int bitMaxRate = (int) Math.min(originalBitrate, maxRate);
			int bitBufSize = (int) (originalBitrate > bufSize ? bufSize : originalBitrate * 2);

			String outputM3u8 = new File(outputDirFile, m3u8FileName).getAbsolutePath();

			// Build FFmpeg command
			FFmpegBuilder fFmpegBuilder = new FFmpegBuilder()
				.setInput(videoInputFile.getAbsolutePath());

			if (useCuda) {
				fFmpegBuilder.addExtraArgs("-hwaccel", "cuda");
			}

			fFmpegBuilder.addOutput(outputM3u8)
				.setAudioCodec("aac")
				.addExtraArgs("-preset", preset)
				.addExtraArgs("-ac", "2")
				.addExtraArgs("-crf", "23")
				.addExtraArgs("-pix_fmt", "yuv420p")
				.addExtraArgs("-vf", "scale='if(gt(iw,ih),1280,-2):if(gt(iw,ih),-2,1280)'") // H.264의 경우, 짝수만 가능
				.addExtraArgs("-colorspace", "bt709")
				.addExtraArgs("-color_primaries", "bt709")
				.addExtraArgs("-color_trc", "bt709")
				.addExtraArgs("-field_order", "progressive")
				.addExtraArgs("-r", "30")
				.addExtraArgs("-g", "60")
				.addExtraArgs("-maxrate", bitMaxRate + "k")
				.addExtraArgs("-bufsize", bitBufSize + "k")
				.addExtraArgs("-hls_time", "4")
				.addExtraArgs("-hls_list_size", "0")
				.addExtraArgs("-hls_segment_filename", new File(outputDirFile, "segment_%03d.ts").getAbsolutePath()) // Segment naming pattern
				.addExtraArgs("-hls_playlist_type", "vod")
				.setVideoCodec(videoCodec)
				.done();

			// Finalize the build
			// FFmpegBuilder fFmpegBuilder = fFmpegOutputBuilder.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(fFmpegBuilder).run();

			log.info("Video successfully converted to HLS format. Playlist: {}", outputM3u8);
		} catch (Exception e) {
			log.error("Failed to convert video to HLS format.", e);
			throw e;
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

	/**
	 * Converts the input video file to export Poster Image
	 *
	 * @param inputImgPath Input video file (e.g., MP4 or MKV)
	 * @param outputImgPath Directory where the JPG files will be saved
	 */
	public byte[] convertImgFormat(Path inputImgPath, Path outputImgPath) throws IOException, InterruptedException {
		// FFmpeg 명령어 실행
		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(inputImgPath.toAbsolutePath().toString()) // 입력 파일
			.addOutput(outputImgPath.toAbsolutePath().toString()) // 출력 파일
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();

		// 변환된 파일 바이트 전달
		return Files.readAllBytes(outputImgPath);
	}

	public Integer getVideoDuration(File videoInputFile) {
		try{
			if (!videoInputFile.exists()) {
				throw new IllegalArgumentException("File does not exist: " + videoInputFile.getAbsolutePath());
			}

			FFmpegProbeResult probeResult = ffprobe.probe(videoInputFile.getAbsolutePath());

			return (int) Math.round(probeResult.getFormat().duration);
		}
		catch (IOException e){
			throw new InternalServerErrorException("비디오 처리하는 데 오류가 발생했습니다.");
		}

	}
}


