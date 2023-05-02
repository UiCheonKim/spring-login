package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class  HomeController {

    private final MemberRepository memberRepository;

    public final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
//        return "redirect:/items"; // localhost:8080 으로 접속시 items 로 리다이렉트
        return "home"; // localhost:8080 으로 접속시 home.html 로 리다이렉트
    }

//    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        // memberId 는 String 인데 Long 으로 받아도 된다. 스프링이 알아서 형변환(타입컨버팅) 해준다.
        // 로그인 처리까지 되는 화면
        // required = false 도 넣어줘야 한다. 이유는 쿠키가 없는 사용자도 있기 때문이다.
        // 만약 required = true 로 하면 쿠키가 없는 사용자는 400 오류가 발생한다.

        if (memberId == null) {
            return "home";
        }

        // 로그인 성공 (쿠키가 있는 사용자)
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }
        // DB 에 찾은 멤버가 없으면 홈으로 보내고
        // 있으면 model attribute 에 담은 다음에 사용자 전용 home 인 loginHome 으로 보낸다.

        model.addAttribute("member", loginMember);

        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member)sessionManager.getSession(request); // return 이 Object 이므로 Member로 형변환 해줘야 한다.

        // 로그인 성공 (쿠키가 있는 사용자)
        if (member == null) {
            return "home";
        }
        // DB 에 찾은 멤버가 없으면 홈으로 보내고
        // 있으면 model attribute 에 담은 다음에 사용자 전용 home 인 loginHome 으로 보낸다.

        model.addAttribute("member", member);

        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        // 세션 관리자에 저장된 회원 정보 조회
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);// return 이 Object 이므로 Member로 형변환 해줘야 한다.

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);

        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);

        return "loginHome";
    }
}