package jpabook.jpashop.api;

import jpabook.jpashop.domain.entity.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // Entity 를 직접 반환하여 좋지 않음
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public ResultResponse getMembersResponse() {
        List<Member> members = memberService.findMembers();
        List<MemberDTO> collect = members
                .stream()
                .map(m -> new MemberDTO(m.getName()))
                .collect(Collectors.toList());

        return new ResultResponse(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class ResultResponse<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String name;
    }

    // Entity 를 사용하여 api
    // Entity 스펙 변경시 혼란 초래
    // Entity 를 외부에 노출해서는 안됨
    // Entity 에 명시된 컬럼에 바인딩되어 잘못된 값이 들어갈 수 있음.
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // DTO를 사용하여 api
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }


    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        CreateMemberResponse(Long id){
            this.id = id;
        }
    }
}
