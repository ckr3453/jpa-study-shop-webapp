package jpabook.jpashop.domain.entity;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문가격

    private int count;  // 주문수량

    // 생성 메서드 (도메인 모델 패턴)
    // 도메인 모델 패턴 : 엔티티 내에서 관련된 핵심 비즈니스 로직을 구현하여 객체지향 특성을 적극 활용하는 것
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        // 주문 상품의 정보를 입력
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 상품 재고에서 제거
       item.removeStock(count);
        return orderItem;
    }


    // 비즈니스 로직
    /**
     * 상품의 주문 취소 (재고 증가)
     */
    public void cancel() {
        getItem().addStock(this.count);
    }

    /**
     * 주문상품 전체 가격 조회
     * @return 상품가격 * 수량
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
