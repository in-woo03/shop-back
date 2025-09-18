package com.inwoo.project.shopback.api.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.Parameter;

import com.inwoo.project.shopback.enums.MemberLevel;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @Parameter(name="ID")
    private Long id;

    @Parameter(name="uuid")
    @NotBlank(message = "UUID는 필수입니다")
    private String uuid;

    @Parameter(name="사용자 이름")
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자 이하여야 합니다")
    private String name;

    @Parameter(name="사용자 이메일")
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
    private String email;

    @Parameter(name="이메일 인증 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime emailVerifiedAt;

    @Parameter(name="비밀번호")
    @JsonIgnore // 응답에서 제외
    private String password;

    @Parameter(name="전화번호")
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호 형식이 올바르지 않습니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;

    @Parameter(name="회원등급")
    @NotNull(message = "회원등급은 필수입니다")
    private MemberLevel memberLevel;

    @Parameter(name="포인트")
    @Min(value = 0, message = "포인트는 0 이상이어야 합니다")
    private Integer points;

    @Parameter(name="이용약관 동의여부")
    @NotNull(message = "이용약관 동의는 필수입니다")
    private Boolean agreeTerms;

    @Parameter(name="마케팅약관 동의여부")
    private Boolean agreeMarketing;

    @Parameter(name="마지막 로그인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @Parameter(name="리프레시 토큰")
    @JsonIgnore // 토큰 정보는 응답에서 제외
    private String refreshToken;

    @Parameter(name="리프레시 토큰 만료시간")
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refreshTokenExpiresAt;

    @Parameter(name="토큰 버전(강제 로그아웃용)")
    @JsonIgnore
    private Integer tokenVersion;

    @Parameter(name="이메일 인증 토큰")
    @JsonIgnore
    private String emailVerificationToken;

    @Parameter(name="비밀번호 재설정 토큰")
    @JsonIgnore
    private String passwordResetToken;

    @Parameter(name="비밀번호 재설정 만료시간")
    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordResetExpires;

    @Parameter(name="계정 활성화 상태")
    private Boolean userActive;

    @Parameter(name="생성일자")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Parameter(name="수정일자")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 이메일 인증 여부 편의 메서드
    @JsonProperty("emailVerified")
    public Boolean isEmailVerified() {
        return emailVerifiedAt != null;
    }
}