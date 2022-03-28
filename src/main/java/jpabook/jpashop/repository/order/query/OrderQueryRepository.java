package jpabook.jpashop.repository.order.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jpabook.jpashop.domain.QDelivery.delivery;
import static jpabook.jpashop.domain.QMember.*;
import static jpabook.jpashop.domain.QOrder.order;
import static jpabook.jpashop.domain.QOrderItem.orderItem;
import static jpabook.jpashop.domain.item.QItem.item;

@Repository
public class OrderQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderQueryRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * Query: 루트 1번, 컬렉션 1번
     * ToOne 관계들을 먼저 조회하고, 여기서 얻은 식별자 orderId로 ToMany 관계인 OrderItem 을
     * 한꺼번에 조회
     * MAP을 사용해서 매칭 성능 향상(O(1))
     */
    public Object findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        List<Long> orderIds = toOrderIds(result);
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        /*
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();
        */
        List<OrderItemQueryDto> orderItems = query
                .select(new QOrderItemQueryDto(
                        orderItem.order.id,
                        item.name,
                        orderItem.orderPrice,
                        orderItem.count
                ))
                .from(order)
                .join(order.orderItems, orderItem)
                .join(orderItem.item, item)
                .where(order.id.in(orderIds))
                .fetch();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        /*
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
                */
        return query
                .select(new QOrderItemQueryDto(
                        orderItem.order.id,
                        item.name,
                        orderItem.orderPrice,
                        orderItem.count
                ))
                .from(orderItem)
                .join(orderItem.item, item)
                .where(orderItem.order.id.eq(orderId))
                .fetch();
    }

    private List<OrderQueryDto> findOrders() {
        /*
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
         */
        return query
                .select(new QOrderQueryDto(
                        order.id,
                        member.name,
                        order.orderDate,
                        order.status,
                        delivery.address))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .fetch();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        /*
        return em.createQuery(
                "select new" +
                        " jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
                */
        return query
                .select(new QOrderFlatDto(
                        order.id,
                        member.name,
                        order.orderDate,
                        order.status,
                        delivery.address,
                        item.name,
                        orderItem.orderPrice,
                        orderItem.count))
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .join(order.orderItems, orderItem)
                .join(orderItem.item, item)
                .fetch();
    }
}
