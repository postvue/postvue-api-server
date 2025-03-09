package com.postvue.feelogserver.global.api.kakao.dto.rsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoMetaDto {
    @JsonProperty("is_end")
    private boolean isEnd;

    @JsonProperty("pageable_count")
    private int pageableCount;

    @JsonProperty("total_count")
    private int totalCount;
}
