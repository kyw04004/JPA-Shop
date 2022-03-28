package jpabook.jpashop.repository.order.simplequery;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.QOrder;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static jpabook.jpashop.domain.QDelivery.delivery;
import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.*;

@Repository
public class OrderSimpleQueryRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderSimpleQueryRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        /*
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
        */
        return query
                .select(new QOrderSimpleQueryDto(
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
}
