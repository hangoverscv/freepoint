package musinsa.freepoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import musinsa.freepoint.domain.Point;
import musinsa.freepoint.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Getter @Setter
@RequiredArgsConstructor
@Tag(name = "Point API", description = "포인트 관련 API")
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;


    @Operation(summary = "포인트 적립")
    @PostMapping("/deposit")
    public void depositPoints(@RequestBody Point point,
                              @RequestParam Long userId) {
        pointService.depositPoints(point, userId);
    }

    @Operation(summary = "포인트 사용")
    @PostMapping("/use")
    public ResponseEntity<Point> usePoints(@RequestBody Point point,
                                           @RequestParam Long userId) {
        pointService.usePoints(point, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포인트 사용 취소")
    @PostMapping("/use/cancel")
    public ResponseEntity<Void> cancelUsePoints(@RequestParam Long orderId,
                                                @RequestParam Long amount) {
        pointService.cancelUsePoints(orderId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포인트 적립 취소")
    @PostMapping("/deposit/cancel")
    public ResponseEntity<Void> cancelDepositPoints(@RequestParam Long pointId) {
        pointService.cancelDepositPoints(pointId);
        return ResponseEntity.ok().build();
    }



}
