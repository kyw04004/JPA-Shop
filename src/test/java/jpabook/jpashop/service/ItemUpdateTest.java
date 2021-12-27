package jpabook.jpashop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
public class ItemUpdateTest {
    
    @Autowired
    EntityManager em;
    
    @Test
    public void updateTest() {
     //   em.find(Book.class, 1L)
    }
}
