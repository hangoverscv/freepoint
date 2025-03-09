package musinsa.freepoint.service;

import musinsa.freepoint.domain.Point;

public interface PointService {

    void depositPoints(Point point, Long userId);
    void usePoints(Point point, Long userId);
    void cancelUsePoints(Long orderId, Long amount);
    void cancelDepositPoints(Long pointId);

}
