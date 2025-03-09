package musinsa.freepoint.service;

import musinsa.freepoint.domain.Order;

public interface OrderService {

    Order creatOrder(Long userId, Long orderAmount);

}
