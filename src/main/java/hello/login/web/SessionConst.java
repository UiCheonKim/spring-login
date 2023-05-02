package hello.login.web;

// 세션에 값을 넣고 뺴고 하기 위한 상수값
public interface SessionConst {
    // 참고로 이 클래스는 생성할 객체가 아니기 때문에 생성되지 않게 abstract 로 추상 클래스로 선언하거나
    // 인터페이스로 쓰는게 더 좋음
    String LOGIN_MEMBER = "loginMember";
}
