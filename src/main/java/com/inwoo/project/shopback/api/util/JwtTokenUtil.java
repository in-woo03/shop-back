package com.inwoo.project.shopback.api.util;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtil {
	
	// Access Token 발급
	public static String createAccessToken(String email, String uuid, Long userId, Integer tokenVersion, String key, long expireTimeMs) {
		Claims claims = Jwts.claims();
		claims.put("email", email);
		claims.put("uuid", uuid);
		claims.put("userId", userId);
		claims.put("tokenVersion", tokenVersion);
		claims.put("tokenType", "access");

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
			.signWith(SignatureAlgorithm.HS256, key)
			.compact();
	}

	// Refresh Token 발급
	public static String createRefreshToken(String email, Long userId, Integer tokenVersion, String key, long expireTimeMs) {
		Claims claims = Jwts.claims();
		claims.put("email", email);
		claims.put("userId", userId);
		claims.put("tokenVersion", tokenVersion);
		claims.put("tokenType", "refresh");

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
			.signWith(SignatureAlgorithm.HS256, key)
			.compact();
	}

	// Claims에서 email 꺼내기
	public static String getEmail(String token, String secretKey) {
		return extractClaims(token, secretKey).get("email").toString();
	}

	// Claims에서 userId 꺼내기
	public static Long getUserId(String token, String secretKey) {
		return Long.valueOf(extractClaims(token, secretKey).get("userId").toString());
	}

	// Claims에서 tokenVersion 꺼내기
	public static Integer getTokenVersion(String token, String secretKey) {
		return Integer.valueOf(extractClaims(token, secretKey).get("tokenVersion").toString());
	}

	// Claims에서 tokenType 꺼내기
	public static String getTokenType(String token, String secretKey) {
		return extractClaims(token, secretKey).get("tokenType").toString();
	}

	// 발급된 Token이 만료 시간이 지났는지 체크
	public static boolean isExpired(String token, String secretKey) {
		try {
			Date expiredDate = extractClaims(token, secretKey).getExpiration();
			return expiredDate.before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

	// Token 유효성 검증
	public static boolean validateToken(String token, String secretKey) {
		try {
			extractClaims(token, secretKey);
			return !isExpired(token, secretKey);
		} catch (Exception e) {
			return false;
		}
	}

	// SecretKey를 사용해 Token Parsing
	private static Claims extractClaims(String token, String secretKey) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}
}
