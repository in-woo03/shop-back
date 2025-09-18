package com.inwoo.project.shopback.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {
    
    @Schema(description = "액세스 토큰")
    private String accessToken;
    
    @Schema(description = "리프레시 토큰")
    private String refreshToken;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "액세스 토큰 만료 시간 (초)")
    private Long expiresIn;
    
    @Schema(description = "사용자 정보")
    private UserInfo userInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사용자 기본 정보")
    public static class UserInfo {
        @Schema(description = "사용자 ID")
        private Long id;
        
        @Schema(description = "사용자 UUID")
        private String uuid;
        
        @Schema(description = "사용자 이름")
        private String name;
        
        @Schema(description = "이메일")
        private String email;
        
        @Schema(description = "회원 등급")
        private String memberLevel;
        
        @Schema(description = "포인트")
        private Integer points;
    }
}