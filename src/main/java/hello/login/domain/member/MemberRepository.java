package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

// Member 를 저장하고 관리하는게 필요
@Slf4j
@Repository
public class MemberRepository {
    // 원래는 MemberRepository 인터페이스를 만들고, 이를 구현하는 클래스를 만들어야 하지만, 여기서는 간단하게 구현

    private static Map<Long, Member> store = new HashMap<>(); // static 사용
    private static long sequence = 0L; // static 사용

    public Member save(Member member) {
        member.setId(++sequence); // id 를 증가시킨다.
        log.info("save: member={}", member);
        store.put(member.getId(), member); // member.getId() 로 Id를 찾고 member 를 저장한다.
        return member;
    }

    public Member findById(Long id) {
        return store.get(id); // id 를 찾아서 반환한다. // Map 이기 때문에 key 를 넣으면 value 를 반환한다.
    }

    public Optional<Member> findByLoginId(String loginId) {
        // Optional<Member> 를 사용하는 이유는 못찾을 수도 있기 때문이다. // null 을 반환하는 것보다 Optional 을 사용하는 것이 좋다.
        // 로그인 할때 Long id 가 아니라 String loginId 가 필요하다.
/*
        List<Member> all = findAll();
        for (Member m : all) {
            if (m.getLoginId().equals(loginId)) {
                return Optional.of(m); // Optional.of() 는 null 이 아닌 값을 감싸는 역할을 한다.
                // Optional 이라는 통이 있는데 그 통 안에 Member 가 들어있을수 도 있고 없을 수도 있다.
            }
        }
        return Optional.empty(); // 예전에 null 을 반환했는데, 요즘은 Optional.empty() 를 반환하는걸로 코드 스타일이 변함
*/

        return findAll().stream() // 리스트를 스트림으로 바꾼다. // loop 를 돈다고 생각하면 된다.
                .filter(m -> m.getLoginId().equals(loginId)) // 필터링 // 이 조건에 만족하는 것만 걸러내서 다음 단계로 전달
                .findFirst(); // 먼저 받은 값 반환 뒤에 받은 애들은 무시된다.
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
        // key 가 id 고 value 가 member 인데 member 가 list 로 전부 변환된다.
        // key 빼고 value 만 다 뽑아서 새로운 ArrayList 를 만들어서 반환한다.
    }

    // 테스트용 메모리 DB 초기화
    public void clearStore() {
        store.clear();
    }
}
