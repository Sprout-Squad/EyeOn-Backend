package Sprout_Squad.EyeOn.global.auth.jwt;

import Sprout_Squad.EyeOn.global.auth.exception.AuthErrorCode;
import Sprout_Squad.EyeOn.global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final MyUserDetailsService userDetailsService;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long validityInSeconds;

    /**
     * 비밀 키를 초기화하는 메서드
     */
    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * JWT 토큰을 생성하는 메서드
     */
    public String createToken(Long id) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();

    }

    /**
     * JWT의 서명을 검증하기 위한 키를 생성하는 메서드
     */
    private Key getSignKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT에서 사용자 정보를 꺼내, Authentic 객체를 만들고 Spring Security에 인증 완료 상태로 등록하는 메서드
     */
    public void setSecurityContext(String token) {
        Claims claims = getClaimsFromToken(token);
        String userId = claims.getSubject();

        // 사용자 ID로 DB에서 사용자 정보 조회 -> 조회한 정보들을 바탕으로 UserPrincipal 객체 생성
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // SpringSecurity에서 사용하는 인증 객체 생성
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * 토큰에서 kakaoId만 뽑아서 리턴해주는 메서드
     */
    public String getKakaoId(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey(secretKey)).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 토큰에 있는 클레임(속성)을 가져오는 메서드
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey(secretKey)).build().parseClaimsJws(token).getBody();
    }

    /**
     * 헤더에서 토큰을 추출하는 메서드
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰이 유효한지 검증하는 메서드
     */
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(getSignKey(secretKey)).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(AuthErrorCode.INVALID_TOKEN_401);
        }

    }

}
