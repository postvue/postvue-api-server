export async function resizeAndCompressImage(
    file: File,
    maxWidth: number,
    maxHeight: number,
    quality: number = 0.9 // 초기 압축 품질 (0.1 ~ 1.0)
): Promise<File> {
    const MAX_SIZE_MB = 2; // 2MB 기준
    const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024; // 바이트 변환

    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => {
            let width = img.width;
            let height = img.height;

            // 🔹 크기 조절 (원본이 maxWidth, maxHeight보다 크면 비율 유지하여 조정)
            if (width > maxWidth || height > maxHeight) {
                if (width > height) {
                    height *= maxWidth / width;
                    width = maxWidth;
                } else {
                    width *= maxHeight / height;
                    height = maxHeight;
                }
            }

            const canvas = document.createElement("canvas");
            const ctx = canvas.getContext("2d");

            canvas.width = width;
            canvas.height = height;
            ctx?.drawImage(img, 0, 0, width, height);

            // 🔹 원본 파일 확장자 확인
            const originalType = file.type;

            // 🔹 PNG는 품질 조절이 불가능하므로 크기 조절만 수행
            function compressImage(currentQuality: number) {
                canvas.toBlob(
                    async (blob) => {
                        if (!blob) {
                            reject(new Error("Failed to process image"));
                            return;
                        }

                        let resizedFile = new File([blob], file.name, { type: originalType });

                        console.log(
                            `📏 현재 해상도: ${width} x ${height}, 품질: ${currentQuality}, 파일 크기: ${(resizedFile.size / 1024).toFixed(2)} KB`
                        );

                        // 🔹 2MB 초과 시 품질을 낮춤 (JPEG/WebP만 가능)
                        // 🔹 2MB 초과 시 품질을 낮춰 재압축
                        if (resizedFile.size > MAX_SIZE_BYTES && currentQuality > 0.3) {
                            console.log(
                                `🔻 2MB 초과 (${(resizedFile.size / 1024 / 1024).toFixed(2)}MB), 품질 낮춤 → ${currentQuality - 0.1}`
                            );
                            resolve(await resizeAndCompressImage(resizedFile, maxWidth, maxHeight, currentQuality - 0.1));
                        } else {
                            resolve(resizedFile);
                        }

                        // ✅ 최적화 완료
                        resolve(resizedFile);
                    },
                    originalType, // 🔹 원본 포맷 유지,
                    quality
                );
            }

            // 초기 품질로 압축 시작
            compressImage(quality);
        };

        img.onerror = reject;
        img.src = URL.createObjectURL(file);
    });
}

export async function resizeImage(
    file: File,
    maxWidth: number,
    maxHeight: number,
): Promise<Blob> {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => {
            let width = img.width;
            let height = img.height;

            // 원본이 maxWidth, maxHeight보다 작으면 그대로 반환
            if (width <= maxWidth && height <= maxHeight) {
                resolve(file);
                return;
            }

            const canvas = document.createElement('canvas');

            if (width > height) {
                if (width > maxWidth) {
                    height *= maxWidth / width;
                    width = maxWidth;
                }
            } else {
                if (height > maxHeight) {
                    width *= maxHeight / height;
                    height = maxHeight;
                }
            }

            canvas.width = width;
            canvas.height = height;

            const ctx = canvas.getContext('2d');
            ctx?.drawImage(img, 0, 0, width, height);

            canvas.toBlob((blob) => {
                if (blob) {
                    const resizedFile = new File([blob], file.name, { type: file.type });
                    resolve(resizedFile);
                } else {
                    reject(new Error('Failed to resize image'));
                }
            }, file.type);
        };
        img.onerror = reject;
        img.src = URL.createObjectURL(file);
    });
}