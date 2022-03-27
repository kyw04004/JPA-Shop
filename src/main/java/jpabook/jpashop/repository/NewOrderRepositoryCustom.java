package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;

import java.util.List;

public interface NewOrderRepositoryCustom {
    List<Order> findAllByString(OrderSearch orderSearch);
    List<Order> findAllByCriteria(OrderSearch orderSearch);
    List<Order> findAllWithMemberDelivery();
    List<Order> findAllWithItem();
    List<Order> findAllWithMemberDelivery(int offset, int limit);
}
