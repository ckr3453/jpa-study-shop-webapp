package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class ItemUpdateTest {

    @Autowired EntityManager em;

    @Test
    public void 변경감지_및_병합_테스트() {
        Book book = em.find(Book.class, 1L);

        // transaction
        book.setName("asdasdas");

        // dirty checking (변경감지)
        // transaction commit;
    }
}
