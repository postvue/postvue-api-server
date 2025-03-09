package com.postvue.feelogserver.global.api.kakao.dto.rsp;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class KakaoSearchResponseDto {
    private List<KakaoPlaceDto> documents;
    private KakaoMetaDto meta;
}