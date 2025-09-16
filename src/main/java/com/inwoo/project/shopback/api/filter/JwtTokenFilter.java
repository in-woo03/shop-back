package com.inwoo.project.shopback.api.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.inwoo.project.shopback.api.user.UserDto;
import com.inwoo.project.shopback.api.user.UserService;
import com.inwoo.project.shopback.api.util.JwtTokenUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// OncePerRequestFilter : 매번 들어갈 때 마다 체크 해주는 필터
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

	private final UserService userService;
	private final String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음 => 로그인 하지 않음
		if(authorizationHeader == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
		if(!authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
		String token = authorizationHeader.split(" ")[1];

		// 전송받은 Jwt Token이 만료되었으면 => 다음 필터 진행(인증 X)
		if(JwtTokenUtil.isExpired(token, secretKey)) {
			filterChain.doFilter(request, response);
			return;
		}

		// Jwt Token에서 email 추출
		String email = JwtTokenUtil.getEmail(token, secretKey);
		Long userId = JwtTokenUtil.getUserId(token, secretKey);
		Integer tokenVersion = JwtTokenUtil.getTokenVersion(token, secretKey);

		// 추출한 email로 UserDto 찾아오기
		UserDto loginUser = userService.findByEmail(email);
		
		// 사용자가 존재하지 않거나 비활성화된 경우
		if (loginUser == null || !loginUser.getUserActive()) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// 토큰 버전 검증 (강제 로그아웃 기능)
		if (!tokenVersion.equals(loginUser.getTokenVersion())) {
			filterChain.doFilter(request, response);
			return;
		}

		// loginUser 정보로 UsernamePasswordAuthenticationToken 발급
		// 임시로 USER 권한 부여 (나중에 역할 시스템 추가 시 수정)
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			loginUser.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		// 권한 부여
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		filterChain.doFilter(request, response);
	}
}
