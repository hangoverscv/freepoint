package musinsa.freepoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import musinsa.freepoint.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@Getter @Setter
@RequiredArgsConstructor
@Tag(name = "Order API", description = "주문 관련 API")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성")
    @PostMapping("/create")
    public void createOrder(@RequestParam Long userId,
                            @RequestParam Long orderAmount){
        orderService.creatOrder(userId, orderAmount);
    }


}
