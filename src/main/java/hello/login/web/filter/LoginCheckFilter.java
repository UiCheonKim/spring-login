package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {
    // 인터페이스에 default 라는 키워드가 있으면 구현체에서 구현하지 않아도 된다.

    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) { // 화이트 리스트가 아닌 경우에만 로그인 체크
                log.info("인증 체크 로직 실행 {}", requestURI);

                HttpSession session = httpRequest.getSession(false);// 세션이 있으면 있는 세션 반환, 없으면 null 반환

                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    // 세션이 null 이거나 attribute 가 null 일 때
                    log.info("미인증 사용자 요청 {}", requestURI);

                    // 로그인 페이지로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    // /login?redirectURL= -> 로그인 후 다시 이전 페이지로 돌아가기 위해 redirectURL 을 파라미터로 넘겨준다.
                    return;
                }
            }
            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e; // 예외 로깅 가능하지만, 톰캣까지 예외를 보내주어야 함 // 왜냐하면 servlet 필터에서 예외가 터져서 올라오는데 여기서 잡아버리면 정상
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     *  화이트 리스트의 경우 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI); // whitelist 에 있는 패턴과 일치하는지 확인
        // whitelist 에 있는 패턴과 일치하면 true, 일치하지 않으면 false
    }
}
