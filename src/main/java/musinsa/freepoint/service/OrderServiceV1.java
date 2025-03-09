package musinsa.freepoint.service;

import lombok.RequiredArgsConstructor;
import musinsa.freepoint.domain.Order;
import musinsa.freepoint.domain.User;
import musinsa.freepoint.repository.OrderRepository;
import musinsa.freepoint.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 implements OrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Order creatOrder(Long userId, Long orderAmount) {

        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));

        Order order = new Order();
        order.setOrderAmount(orderAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setUser(User.of(userId));
        return orderRepository.save(order);
    }
}
