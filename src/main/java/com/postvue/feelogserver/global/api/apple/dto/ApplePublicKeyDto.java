package com.postvue.feelogserver.global.api.apple.dto;

import lombok.Data;

@Data
public class ApplePublicKeyDto {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;
}
