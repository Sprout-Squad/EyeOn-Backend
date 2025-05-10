package Sprout_Squad.EyeOn.global.auth.jwt;

import Sprout_Squad.EyeOn.global.auth.exception.InvalidTokenException;
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
        jakarta.servlet.http.HttpServletResponse response = (jakarta.servlet.http.HttpServletResponse) servletResponse;
        String token = jwtTokenProvider.resolveToken(request);

        /**
         * DispatcherServlet까지 도달하기 전에 에러가 터지기 때문에
         * Filter 안에서 던진 예외는 GlobalExceptionHandler가 못 잡음
         */
        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);  // 유효하지 않으면 Exception
                jwtTokenProvider.setSecurityContext(token);
            } catch (InvalidTokenException e) {
                response.setStatus(401);  // UNAUTHORIZED
                response.setContentType("application/json;charset=UTF-8");
                String body = """
                    {
                      "isSuccess": false,
                      "code": "INVALID_TOKEN_401",
                      "httpStatus": 401,
                      "message": "토큰 값을 확인해주세요.",
                      "data": null,
                      "timeStamp": "%s"
                    }
                    """.formatted(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                response.getWriter().write(body);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }


}
