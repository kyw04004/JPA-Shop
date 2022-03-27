package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewItemRepository extends JpaRepository<Item, Long> {
}
