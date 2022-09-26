package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.entity.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

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

    // Good Case : N + 1 문제를 fetch join 으로 최적화 (권장)
    // 결과적으로 join 쿼리를 통해 단 한번의 실행으로 결과를 가져옴
    // 남은 과제 : entity 를 대상으로 조회하지 않기 (원하지 않는 결과를 쿼리로 같이 조회함)
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDTO> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                .map(SimpleOrderDTO::new)
                .collect(Collectors.toList());
    }

    // Good Case : 원하는 결과 를 대상으로 조회
    // JPQL 대상을 DTO(Repository) 로 설정하여 원하는 결과만 쿼리로 조회
    // 남은 과제 : 대신 결과가 정해져있기 때문에 공용으로 재사용되기 힘듬 - 이런 최적화용 쿼리를 위한 Repository 를 따로 만들어서 관리
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDTO> ordersV4(){
        return orderSimpleQueryRepository.findOrderDTOs();
    }

    // 정리
    // 엔티티를 직접 사용하지말고 DTO 를 만들어서 활용한다.
    // 필요하면 fetch join 으로 성능을 최적화 한다. -> 대부분 해결 가능
    // 그래도 되지 않으면 DTO 로 직접 조회하는 방법을 사용한다.
    // 최후의 방법은 직접 네이티브 SQL 을 작성한다.

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
