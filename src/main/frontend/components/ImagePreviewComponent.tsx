import React, {useState} from "react";

interface ImagePreviewComponentProps {
    imgPath:string;
}

const ImagePreviewComponent:React.FC<ImagePreviewComponentProps> = ({imgPath}) =>{
    const [isImageLoaded, setIsImageLoaded] = useState<boolean>(true); // ✅ 이미지 로드 상태 관리

    return <>
        {isImageLoaded ? (
            <img
                src={imgPath}
                alt="Preview"
                style={{ maxWidth: "300px",  display: isImageLoaded ? "block" : "none" , borderRadius: '10px'}}
                onError={() => setIsImageLoaded(false)} // ✅ 이미지 로드 실패 시 숨김 처리
            />
        ) : null}
    </>
}
export default ImagePreviewComponent;