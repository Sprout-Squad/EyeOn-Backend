package Sprout_Squad.EyeOn.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 요청이 들어올 때마다 실행되어 JWT를 검증하는 메서드
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = jwtTokenProvider.resolveToken(request);

        /**
         * 토큰이 있으면, 유효성 검사를 진행
         * 유효성 검사를 통과하면, 인증 컨텍스트를 설정
         */
        if (token != null) {
            if(jwtTokenProvider.validateToken(token)) {
                jwtTokenProvider.setSecurityContext(token);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
