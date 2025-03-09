import React, { useEffect, useRef } from "react";
import Hls from "hls.js";


const HlsPlayer = ({ src="", width = "100%", height = "auto" }) => {
    const videoRef = useRef<HTMLVideoElement>(null);

    useEffect(() => {
        const video = videoRef.current;
        if (!video) return;

        if (Hls.isSupported()) {
            const hls = new Hls();
            hls.loadSource(src);
            hls.attachMedia(video);

            hls.on(Hls.Events.MANIFEST_PARSED, () => {
                video.play();
            });

            return () => {
                hls.destroy();
            };
        } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
            video.src = src;
            video.addEventListener("loadedmetadata", () => {
                video.play();
            });
        }

        return () =>{
            ("")
        }
    }, [src]);

    return <video ref={videoRef} controls width={width} height={height} />;
};

export default HlsPlayer;