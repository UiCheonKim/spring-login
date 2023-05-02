package hello.login.web.member;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository; // 의존 관계 주입

    @GetMapping("/add") // 회원 등록
    public String addForm(@ModelAttribute("member") Member member) {
        // @ModelAttribute 은 Item 객체를 생성하고, 요청 파라미터 값을 프로퍼티 접근법(SetXxx)으로 입력해준다.
        // ex) @ModelAttribute("hello") Item item => model.addAttribute("hello", item);
        // @ModelAttribute("member") 에서 ("member") 는 생략 가능하다. // 첫번째 글자만 소문자로 바꿔서 넣어준다.
        return "members/addMemberForm"; // 회원가입 form 으로 보냄
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        // BindingResult - 검증 오류 결과를 보관한다.
        if (bindingResult.hasErrors()) { // 에러가 있으면
            return "members/addMemberForm"; // 회원가입 form 으로 보냄
        }

        memberRepository.save(member); // 회원 저장
        return "redirect:/"; // 홈 화면으로 리다이렉트
    }

}
