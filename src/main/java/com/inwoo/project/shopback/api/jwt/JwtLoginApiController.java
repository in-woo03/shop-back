package com.inwoo.project.shopback.api.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inwoo.project.shopback.api.dto.LoginRequest;
import com.inwoo.project.shopback.api.dto.LoginResponse;
import com.inwoo.project.shopback.api.user.UserDto;
import com.inwoo.project.shopback.api.user.UserService;
import com.inwoo.project.shopback.api.util.JwtTokenUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginApiController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${jwt.secret}")
	private String secretKey;
	
	@Value("${jwt.access-token-expire-time}")
	private long accessTokenExpireTime;
	
	@Value("${jwt.refresh-token-expire-time}")
	private long refreshTokenExpireTime;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			// 사용자 조회
			UserDto user = userService.findByEmail(loginRequest.getEmail());
			if (user == null) {
				return ResponseEntity.badRequest().body("이메일 또는 비밀번호가 올바르지 않습니다.");
			}

			// 계정 활성화 상태 확인
			if (!user.getUserActive()) {
				return ResponseEntity.badRequest().body("비활성화된 계정입니다.");
			}

			// 비밀번호 검증
			if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
				return ResponseEntity.badRequest().body("이메일 또는 비밀번호가 올바르지 않습니다.");
			}

			// 토큰 버전 증가 (새로운 로그인)
			Integer newTokenVersion = (user.getTokenVersion() != null ? user.getTokenVersion() : 0) + 1;

			// JWT 토큰 생성
			String accessToken = JwtTokenUtil.createAccessToken(
				user.getEmail(), user.getUuid(), user.getId(), newTokenVersion, secretKey, accessTokenExpireTime);
			String refreshToken = JwtTokenUtil.createRefreshToken(
				user.getEmail(), user.getId(), newTokenVersion, secretKey, refreshTokenExpireTime);

			// 리프레시 토큰 DB에 저장
			userService.updateRefreshToken(user.getId(), refreshToken, newTokenVersion);

			// 마지막 로그인 시간 업데이트
			userService.updateLastLoginAt(user.getId());

			// 응답 생성
			LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
				.id(user.getId())
				.uuid(user.getUuid())
				.name(user.getName())
				.email(user.getEmail())
				.memberLevel(user.getMemberLevel().toString())
				.points(user.getPoints())
				.build();

			LoginResponse response = LoginResponse.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.tokenType("Bearer")
				.expiresIn(accessTokenExpireTime / 1000)
				.userInfo(userInfo)
				.build();

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("로그인 처리 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/info")
	public ResponseEntity<?> userInfo(Authentication auth) {
		try {
			String email = auth.getName();
			UserDto loginUser = userService.findByEmail(email);
			
			if (loginUser == null) {
				return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
			}

			LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
				.id(loginUser.getId())
				.uuid(loginUser.getUuid())
				.name(loginUser.getName())
				.email(loginUser.getEmail())
				.memberLevel(loginUser.getMemberLevel().toString())
				.points(loginUser.getPoints())
				.build();
			
			return ResponseEntity.ok(userInfo);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("사용자 정보 조회 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/admin")
	public String adminPage() {
		return "관리자 페이지 접근 성공";
	}
}