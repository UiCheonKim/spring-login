package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    // preHandle 과 postHandle 은 예외인 경우 호출되지 않는다.
    // String uuid; // 싱글톤으로 만들어지기 때문에 큰일남 // 다른 요청이 들어오면 덮어씌워짐
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI(); // HttpServletRequest 로 받아온다. // 요청 URI
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);
        // afterCompletion 에서 쓰기 위해 저장
        // http request 하나의 사용자가 보장이 되기 때문에 이렇게 저장해도 된다.

        // @RequestMapping : HandlerMethod
        // 정적 리소스 : ResourceHttpRequestHandler

        // 핸들러 정보는 어떤 핸들러 매핑을 사용하는가에 따라 달라진다.
        if (handler instanceof HandlerMethod) {
            // 스프링을 사용하면 일반적으로 @Controller @RequestMapping 을 활용한 핸들러 매핑을 사용하는데
            // 이 경우 핸들러 정보로 HandlerMethod 가 넘어온다
            HandlerMethod hm = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
            log.trace("메서드 = {}", hm.getMethod());
            log.trace("리턴타입 = {}", hm.getReturnType());
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        // handler - 핸들러 정보 // 어떤 컨트롤러가 호출되었는지 알 수 있다.

        return true; // 핸들러 어뎁터 호출되고 컨트롤러 호출된다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String)request.getAttribute(LOG_ID);// preHandle 에서 저장한거 꺼내옴

        log.info("RESPONSE [{}][{}][{}]", uuid, requestURI, handler);

        if (ex != null) { // 예외가 발생한 경우
            log.error("afterCompletion error!!", ex);
        }
    }
}
