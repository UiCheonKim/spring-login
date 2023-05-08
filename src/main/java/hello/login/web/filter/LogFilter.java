package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 *      스프링 시큐리티 같은것들도 다 그냥 필터로 이렇게 돌아가는거다.
 *      필터를 쓰든 뭐 이렇게 해서 앞에서 다 막고 그런거다 -> 물론 보안이기에 복잡하긴 하지만 이런것에서 시작한다
 */

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // ServletRequest 는 HttpServletRequest 의 부모이므로 다운 케스팅이 가능하다.
        // ServletRequest 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스로 HTTP 사용한다면 다운 케스팅 하면 된다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();// 요청 URI

        String uuid = UUID.randomUUID().toString(); // 요청한거 구분위해

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response);
            // 이 부분 빠지면 안됨 // 다음 필터 호출 // 다음 필터가 없으면 서블릿 호출
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
