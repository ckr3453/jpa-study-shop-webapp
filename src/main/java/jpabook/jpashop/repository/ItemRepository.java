package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null){
            em.persist(item);
        } else {
            // 병합 방식
            // item은 영속성 컨텍스트로 관리되지 않고 반환되는 객체가 관리됨.
            // 모든 속성이 덮어쓰기됨. (일부 필드가 null 일 경우 null 로 전부 업데이트 됨)
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
