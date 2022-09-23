package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.entity.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ?(many or one) to one 관계에서의 api 성능최적화 예제
 * Order
 * Order to Member
 * Order to Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    // Worst Case : DTO 안쓰고 Entity 로 뽑는 경우
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {

        // 1. Entity 속성간의 연관 관계에 따라 무한루프에 빠짐 (Order, Member) -> @JsonIgnore 로 한쪽에서 끊어줘야 함 (안좋음)
        // 2. 연관관계중 로딩 방식이 Lazy(지연로딩) 인 경우 -> 강제로 지연 로딩 전부 초기화-실행 (안좋음)
        // 2-1. 그렇다고 이걸 Eager(즉시로딩)으로 바꾸면 안된다. -> 설계가 무너짐, 다른 api 호출 시 활용 불가능 -> 성능 최적화 불가능
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : allByCriteria) {
            order.getMember().getName();      // Lazy 대상 객체를 강제로 실행(초기화) 하여 결과를 가져옴.
            order.getDelivery().getAddress(); // Lazy 대상 객체를 강제로 실행(초기화) 하여 결과를 가져옴.
            order.getOrderItems();
        }
        return allByCriteria;
    }

    // Worst Case : DTO 를 따로 만들어서 반환 but N + 1 문제 발생
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDTO> ordersV2() {
        // Order 2개 결과 나옴
        // N + 1 : N번 만큼 추가로 쿼리가 실행되는 문제
        // 회원 Lazy 쿼리 N + 배송 Lazy 쿼리 N + 원래쿼리 1
        // 최악의 경우 Order 결과가 2개면 2 + 2 + 1 = 5번 쿼리를 실행함..
        List<Order> allByCriteria = orderRepository.findAllByCriteria(new OrderSearch());

        // DTO 안에서 Lazy가 2번 초기화 되기 때문에 Order 갯수 * 2번의 쿼리가 나감
        return allByCriteria.stream()
                .map(SimpleOrderDTO::new)
                .collect(Collectors.toList());
    }

    @Data
    static class SimpleOrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDTO(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // Lazy 초기화 : 영속성 컨텍스트가 내부에서 조회 후 없으면 DB에서 가져옴
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // Lazy 초기화 : 영속성 컨텍스트가 내부에서 조회 후 없으면 DB에서 가져옴
        }
    }

}
