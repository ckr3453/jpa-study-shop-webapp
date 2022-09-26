package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // 실무용으로 사용되는, 복잡한 최적화용 쿼리들만 따로 뽑아서 관리!
    // 특정 클래스에 의존하는 관계를 끊어내기 위해서 (유지보수시 좋음)
    public List<OrderSimpleQueryDTO> findOrderDTOs() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO(o.id, m.name, o.orderDate, o.status, d.address)" +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDTO.class)
                .getResultList();
    }

}
