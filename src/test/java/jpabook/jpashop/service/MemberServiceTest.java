package jpabook.jpashop.service;

import jpabook.jpashop.domain.entity.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // 테스트는 완료시점에 커밋되지않고 롤백됨.
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(savedId));

    }

    @Test
    public void 중복회원_예외처리() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        Member member1 = new Member();
        member1.setName("kim");

        //when
        memberService.join(member);

        //then
        assertThrows(IllegalStateException.class, () -> memberService.join(member1));

    }

}