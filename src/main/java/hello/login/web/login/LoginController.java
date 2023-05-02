package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        log.info("로그인 화면으로 이동");
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
         if(bindingResult.hasErrors()){
             return "login/loginForm";
         }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

         if(loginMember == null) {
             bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // reject 는 global error
             return "login/loginForm"; // 다시 로그인 화면으로 보내서 아이디와 비밀번호를 다시 입력하게 한다.
         }

        // 로그인 성공 처리
        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키 (브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        // 생성한 쿠키를 서버에서 http 응답 보낼 때 response 에 넣어 보낸다.
        // 쿠키의 이름은 memberId 이고, 값은 로그인한 회원의 id 이다.
        // 웹 브라우저는 종료 전까지 회원의 Id 를 서버에 계속 전달한다.
        response.addCookie(idCookie);


        return "redirect:/"; // 로그인 되면 홈으로
    }

//    @PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // reject 는 global error
            return "login/loginForm"; // 다시 로그인 화면으로 보내서 아이디와 비밀번호를 다시 입력하게 한다.
        }

        // 로그인 성공 처리

        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/"; // 로그인 되면 홈으로
    }

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if(loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다."); // reject 는 global error
            return "login/loginForm"; // 다시 로그인 화면으로 보내서 아이디와 비밀번호를 다시 입력하게 한다.
        }

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(); // 있으면 재사용 없으면 신규 생성
        // request.getSession(true); // 있으면 있는 세션 반환, 없으면 신규 세션을 생성 // default
        // request.getSession(false); // 있으면 있는 세션 반환, 없으면 null 반환
        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);


        return "redirect:/"; // 로그인 되면 홈으로
    }

//    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        // HttpServletRequest 사용이유
        // 왜냐면 쿠키에 있는 값을 꺼내서 만들어 놓은 걸 expire 시키기 때문에
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션이랑 그 안에 있는 데이터 다 지워버림
        }
        return "redirect:/";
    }


    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);// 쿠키 삭제
        cookie.setMaxAge(0); // 0 으로 설정하면 쿠키가 바로 삭제된다.
        response.addCookie(cookie);
    }
}
