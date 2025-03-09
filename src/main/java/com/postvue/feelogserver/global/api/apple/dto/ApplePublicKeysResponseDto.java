package com.postvue.feelogserver.global.api.apple.dto;

import lombok.Data;
import java.util.List;

@Data
public class ApplePublicKeysResponseDto {
    private List<ApplePublicKeyDto> keys;
}
