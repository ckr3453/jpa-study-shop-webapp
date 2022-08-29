package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // for proxy (jpa 스펙상 인자없는 생성자 필요)
    // jpa 구현 라이브러리가 객체를 생성할 때 리플렉션 같은 기술을 사용할수 있도록 지원해야해서
    protected Address() {

    }
}
