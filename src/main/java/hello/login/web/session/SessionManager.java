package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리
 * 쿠키 값은 임의로 변경 가능하므로, 보안에 취약하다.
 * 쿠키에 보관된 정보는 훔쳐 갈 수 있다.
 * 해커가 쿠키를 한번 훔쳐가면, 그 쿠키로 계속 요청이 가능하다.
 * -> 해커가 쿠키를 훔쳐가도 피해를 입지 않도록, 서버에서 세션 관리를 통해 해결한다. (세션 id 를 통해)
 * -> 세션 id 는 해커가 임의로 만들 수 없는 값이어야 한다. (UUID 사용)
 * -> 세션 id 를 쿠키에 담아서 사용한다.
 */
@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();
    // String - 세션 id, Object - 세션에 보관할 객체

    /**
     *  세션 생성
     *  sessionId 생성 (임의의 추정 불가능한 랜덤 값)
     * 세션 저장소에 sessionId와 보관할 값 저장
     * sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
     */
    public void createSession(Object value, HttpServletResponse response) {

        // 세션 id 를 생성하고, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, value);

        // 쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId); // 쿠키 이름은 SESSION_COOKIE_NAME, 값은 sessionId
        response.addCookie(mySessionCookie); // 응답에 쿠키를 담아서 보낸다.
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
/*
        Cookie[] cookies = request.getCookies(); // 쿠키 찾기 // 쿠키는 여러개가 있을 수 있으므로 배열로 받는다.
        if (cookies == null) { // 쿠키가 없으면 null
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SESSION_COOKIE_NAME)) { // 쿠키 이름이 SESSION_COOKIE_NAME 과 같으면
                return sessionStore.get(cookie.getValue()); // 세션 저장소에서 세션 id 로 세션 객체를 찾아서 반환
            }
        }
        return null; // 쿠키가 없으면 null
*/
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue()); // 세션 저장소에서 세션 객체를 삭제
        }
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        // 쿠키 찾기 // 쿠키는 여러개가 있을 수 있으므로 배열로 받는다.
        if (request.getCookies() == null) { // 쿠키가 없으면 null
            return null;
        }

        return Arrays.stream(request.getCookies()) // 배열을 stream 으로 바꿔준다.
                .filter(cookie -> cookie.getName().equals(cookieName)) // 쿠키 이름이 cookieName 과 같은 것을 찾는다.
                .findAny() // 병렬처리 할 일이 별로 없긴 때문에 findFirst() 나 findAny() 를 쓰면 된다.
                .orElse(null); // 없으면 null
    }

}
