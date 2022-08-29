package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 읽기전용 트랜잭션을 적용하여 성능 최적화 (일부 기능 스킵)
@RequiredArgsConstructor    // final 키워드가 붙은 필드로 생성자
//@AllArgsConstructor 모든 필드로 생성자
public class MemberService {

    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional  // 쓰기는 적용하면 안됨
    public Long join(Member member){
        validateDuplicateMember(member);    // 중복회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //회원 조회
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
