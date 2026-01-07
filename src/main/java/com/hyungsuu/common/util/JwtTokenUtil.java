package com.hyungsuu.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.*;

@Component
public class JwtTokenUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
	
	
	private Date nowDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
	
	@Value("${jwt.secret.Key}")
	private String secret;
	
	@Value("${jwt.userId}")
	private String jwtUserId;
	
	@Value("${jwt.userAuth}")
	private String jwtUserAuth;
	/*
	 * TOKEN으로 유저명 찾아옴!!
	 * */
	public String getUsernameFromToken(String token) {
		if(validateToken(token)) {
			return getClaimFromToken(token, Claims::getSubject);
		}
		return "";
	}
	
	/*
	 * TOKEN으로 데이터 찾아옴!!
	 * */
	public String getClaimsDataFromToken(String name,String token) {
		if(validateToken(token)) {
			Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
			String realname = claims.get(name,String.class);
			return realname;
		}
		return "";
	}
	/*
	 * TOKEN으로 유효시간 찾아옴!!
	 * */
	
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);

	}
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		if(validateToken(token)) {
			final Claims claims = getAllClaimsFromToken(token);
			
			return claimsResolver.apply(claims);
		}
		return null;
	}
	private Claims getAllClaimsFromToken(String token) {
		Claims claims= Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		return claims;
	}
	public Boolean isTokenExpired(String token) {
		Boolean validateToken = validateToken(token);
		if(validateToken) {
			Date expiration = getExpirationDateFromToken(token);
			int check = expiration.compareTo(nowDate);
			if(0==check){
				return false;
			}else if(1==check) {
				return true;
			}else if(-1==check) {
				return false;
			}else{
				return false;
			}
		}
		return validateToken;
	}
	
	/*
	 * 토큰생성
	 * */
	public String generateToken(String userId, String userAuth, long JWT_TOKEN_VALIDITY) {
	
		Claims claims = Jwts.claims().setSubject(userId);
    	// 업무에 따라 추가 및 삭제 필요
		claims.put(jwtUserId, userId);
    	claims.put(jwtUserAuth, userAuth);
		return doGenerateToken(claims, JWT_TOKEN_VALIDITY);
	}
	
	private String doGenerateToken(Map<String, Object> claims, long JWT_TOKEN_VALIDITY) {
		
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(nowDate)
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//				.signWith(SignatureAlgorithm.RS512, secret)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}
	public Boolean validateToken(String token) {
		try{
			getAllClaimsFromToken(token);
			return true;
		} catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }

    	return false;
	}
}