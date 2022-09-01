package jpabook.jpashop.domain.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class BookForm {

    private Long id;

    // 상품 공통 속성
    @NotEmpty(message = "상품명은 필수입니다.")
    private String name;
    private int price;
    private int stockQuantity;

    // 책 속성
    private String author;
    private String isbn;
}
