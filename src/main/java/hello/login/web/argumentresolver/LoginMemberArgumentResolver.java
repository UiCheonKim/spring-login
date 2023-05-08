package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);// 파라미터에 에노테이션이 붙어있는지 물어보는거

        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());// 파라미터 타입이 Member.class 인지 물어보는거

        return hasLoginAnnotation && hasMemberType; // 둘다 true 여야 true 가 된다
    }

    // false 면 이 메서드는 실행되지 않는다.
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();// HttpServletRequest 가 필요해서 이렇게 꺼내서 쓴다.

        HttpSession session = request.getSession(false); // true 하면 세션이 만들어지는데 의미없는 세션을 만들 필요가 없다. false 로 하면 세션이 없으면 null 을 반환한다.

        if (session == null) { // 세션이 null 이면 Member 에 null 을 넣음
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
