package hello.login.domain.member;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

// 회원 정보를 담는 클래스
@Data // getter, setter, toString, equals, hashCode 자동 생성
public class Member {
    private Long id; // 회원 아이디 // 데이터베이스에 저장되어 관리되는 아이디
    @NotEmpty // 빈 값이면 안된다. // @NotEmpty는 null과 빈 문자열을 허용하지 않는다.
    private String loginId; // 로그인 아이디 // 사용자가 직접 입력한 아이디
    @NotEmpty
    private String name; // 회원 이름
    @NotEmpty
    private String password; // 비밀번호
}
