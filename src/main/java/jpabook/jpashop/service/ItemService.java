package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 변경감지기능 (dirty checking) 을 활용하여 정보 수정
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        // 영속 상태임 (영속성 컨텍스트가 관리 중)
        Item item = itemRepository.findOne(itemId);

        // 추가적으로 레포지토리에 저장할 필요 x, 자동으로 커밋, flush
        // 속성을 선택하여 저장할 수 있음
        // 예시로 setter 를 썼지만 setter 사용하지말고 엔티티 내에서 의미있는 메소드로 구현해야함. (setter 사용 시 추적이 힘듬)
        item.setId(itemId);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItem() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
