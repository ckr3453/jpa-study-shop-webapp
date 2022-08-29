package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // 컬렉션 객체는 필드에서 초기화함으로써 NullPointerException 방지 (Best practice)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
