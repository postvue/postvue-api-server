# Postvue API SERVER
- 지도기반 소셜미디어

### vaddin 실행 방법
- ./gradlew clean vaadinBuildFrontend : 프론트엔드 관련 파일 생성

---

## 주요 기능

이력서에 작성된 **비동기 아키텍처 전환**, **영상 처리 파이프라인 최적화 (FFmpeg, MinIO)** 및 **장애 대응 시스템 (DLQ, Discord)** 등 주요 기술 사항이 프로젝트 내부 어디에 구현되어 있는지 확인하실 수 있는 가이드라인입니다.

### 1. 비동기 영상 업로드 및 트랜스코딩 아키텍처
* **서버의 즉시 응답 반환 및 RabbitMQ 대기열 등록**
  * 사용자가 영상을 업로드하면, 대기 시간을 줄이기 위해 첫 프레임의 썸네일(Poster)만 먼저 생성 및 저장한 뒤 비디오 전처리(인코딩) 작업은 메시지 큐에 등록하여 즉시 응답을 반환합니다.
  * [PostsService.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/posts/service/PostsService.java#L1480-L1491): 포스트 업로드 비즈니스 로직 중 비동기 처리 분기점
  * [VideoConversationProducer.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/messagequeue/service/producer/VideoConversationProducer.java): RabbitMQ의 비디오 처리 전용 큐로 작업을 발행(Publish)

* **RabbitMQ 워커(Worker)를 통한 비동기 영상 처리**
  * 다중 스레드로 작동하는 컨슈머가 백그라운드에서 영상을 소비(Consume)하여 파일 처리를 안전하게 진행합니다.
  * [RabbitMQConfig.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/core/config/RabbitMQConfig.java): Queue, Exchange, Routing Key 등 메시지 브로커 기본 인프라 설정
  * [VideoConversionConsumer.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/messagequeue/service/consumer/VideoConversionConsumer.java#L65-L100): `@RabbitListener` 어노테이션으로 동시 처리량(`concurrency = "3-12"`)을 유연하게 제어하며 큐를 소비

### 2. FFmpeg 및 MinIO 연동 (HLS 스트리밍 최적화)
* **FFmpeg를 활용한 영상 압축, HLS(.m3u8, .ts) 변환 및 CUDA(GPU) 가속**
  * 모바일 환경의 빠른 스트리밍을 지원하기 위해 미디어를 세그먼트(.ts)로 조각화하고 인덱스 파일(.m3u8)을 빌드합니다. `useCuda` 설정을 통한 하드웨어 가속 옵션도 준비되어 있습니다.
  * [FfmpegProcessingService.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/externallib/ffmpeg/FfmpegProcessingService.java#L111-L170): `convertToHLS` 메소드에서 FFmpeg 래퍼 라이브러리를 통해 HLS 인코딩 옵션(Preset, CRF, segment naming 등) 및 CUDA 가속(`-hwaccel cuda`) 등을 쉘 명령어 형태로 실행합니다.

* **MinIO 오브젝트 스토리지 적재**
  * 인코딩 완료된 HLS 조각 파일들을 클라우드 스토리지에 동적 저장하여 안정적인 재생 속도를 확보합니다.
  * [MinioCloudService.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/cloud/service/MinioCloudService.java): MinIO 클라이언트를 연동하여 결과 파일들을 업로드하는 모듈

### 3. 메시지 재처리 및 DLQ / Discord 알림 시스템
* **장애 대응을 위한 무한 재처리 방지 및 DLQ(Dead Letter Queue) 설계**
  * 메시지 소비 중 네트워크 장애, OOM 등으로 예외 발생 시 최대 3회까지 재시도 후 Parking Lot 큐로 안전하게 격리합니다.
  * [DlxProcessingErrorHandler.java](postvue/postvue-api-server/src/main/java/com/postvue/feelogserver/app/messagequeue/handler/DlxProcessingErrorHandler.java): 에러 핸들러로써 재시도 횟수 제한(`maxRetryCount = 3`)을 제어하고, 한도 초과 시 메시지 격리 및 ACK 처리하는 핵심 모듈
