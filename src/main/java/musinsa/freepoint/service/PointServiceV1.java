package musinsa.freepoint.service;

import musinsa.freepoint.domain.*;
import musinsa.freepoint.repository.OrderRepository;
import musinsa.freepoint.repository.PointRepository;
import musinsa.freepoint.repository.PointTrackRepository;
import musinsa.freepoint.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PointServiceV1 implements PointService{

    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PointTrackRepository pointTrackRepository;

    @Value("${point.max.amount}")
    private Long maxPointAmount;

    public PointServiceV1(PointRepository pointRepository, UserRepository userRepository, OrderRepository orderRepository, PointTrackRepository pointTrackRepository) {
        this.pointRepository = pointRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.pointTrackRepository = pointTrackRepository;
    }

    // 포인트 적립
    public void depositPoints(Point point, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

        if (point.getAmount() < 1 || point.getAmount() > maxPointAmount) {
            throw new IllegalArgumentException("1회 적립 가능 금액은 1원 이상 10만원 이하입니다");
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expirationDateTime = point.getExpirationDate() != null ? point.getExpirationDate() : now.plusDays(365);
        if (expirationDateTime.isAfter(now.plusYears(5))) {
            throw new IllegalArgumentException("만료일은 5년을 초과할 수 없습니다.");
        }

        List<Point> points = getValidPointsByUser(user);

        // 전체 만료되지 않은 포인트 합산
        Long totalAvailablePoints = points.stream()
                .mapToLong(Point::getAvailableAmount)
                .sum();

        // 사용자별 보유 포인트 한도 체크
        Long totalAmount = Optional.ofNullable(totalAvailablePoints).orElse(0L) + point.getAmount();
        if (totalAmount > user.getMaxPoints()) {
            throw new IllegalArgumentException("보유 가능 포인트 한도를 초과했습니다.");
        }

        Point newPoint = new Point();
        newPoint.setAmount(point.getAmount());
        newPoint.setAvailableAmount(point.getAmount());
        newPoint.setCreatedDate(now);
        newPoint.setExpirationDate(expirationDateTime);
        newPoint.setIsManual(point.getIsManual());
        newPoint.setIsAvailable(true);
        newPoint.setUser(user);
        pointRepository.save(newPoint);

        PointTrack track = new PointTrack();
        track.setAmount(point.getAmount());
        track.setTrackDate(now);
        track.setPointType(PointType.DEPOSIT);
        track.setPoint(newPoint);
        pointTrackRepository.save(track);


    }

    // 포인트 사용
    public void usePoints(Point point, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

        Order order = orderRepository.findByOrderNumber(point.getOrderNumber())
                .orElseThrow(() -> new RuntimeException("주문 정보가 없습니다."));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 사용자가 주문한 내역이 아닙니다.");
        }

        List<Point> points = getValidPointsByUser(user);

        // 전체 만료되지 않은 포인트 합산
        Long totalAvailablePoints = points.stream()
                .mapToLong(Point::getAvailableAmount)
                .sum();

        if (point.getAmount() > totalAvailablePoints) {
            throw new IllegalArgumentException("포인트 부족");
        }

        Long remainingOrderAmount = order.getOrderAmount() - Optional.ofNullable(order.getUsePointAmount()).orElse(0L);
        if (point.getAmount() > remainingOrderAmount) {
            throw new IllegalArgumentException("포인트 사용 금액이 주문 금액 초과");
        }

        // is_manual이 true인 포인트 중에서 그 안에서 만료일이 짧은 순으로 정렬
        List<Point> manualPoints = points.stream()
                .filter(Point::getIsManual)
                .sorted(Comparator.comparing(Point::getExpirationDate))
                .collect(Collectors.toList());

        // is_manual이 false인 포인트 중에서 만료일이 짧은 순으로 정렬
        List<Point> autoPoints = points.stream()
                .filter(p -> !p.getIsManual())
                .sorted(Comparator.comparing(Point::getExpirationDate))
                .collect(Collectors.toList());

        List<Point> sortedPoints = new ArrayList<>();
        sortedPoints.addAll(manualPoints);
        sortedPoints.addAll(autoPoints);

        Long remainingAmount = point.getAmount();

        LocalDateTime now = LocalDateTime.now();

        for (Point po : sortedPoints) {
            if (remainingAmount == 0) break;

            if (po.getAvailableAmount() > 0) {
                Long usedAmount = Math.min(remainingAmount, po.getAvailableAmount());
                po.setAvailableAmount(po.getAvailableAmount() - usedAmount);
                po.setOrderNumber(point.getOrderNumber());
                pointRepository.save(po);

                remainingAmount -= usedAmount;

                PointTrack track = new PointTrack();
                track.setAmount(usedAmount);
                track.setTrackDate(now);
                track.setPointType(PointType.USE);
                track.setPoint(po);
                track.setOrder(order);
                pointTrackRepository.save(track);
            }
        }

        order.setUsePointAmount(Optional.ofNullable(order.getUsePointAmount()).orElse(0L) + point.getAmount());
        orderRepository.save(order);

    }

    // 사용 취소
    public void cancelUsePoints(Long orderId, Long amount) {
        List<PointTrack> tracks = pointTrackRepository.findByOrderId(orderId)
                .stream()
                .sorted(Comparator.comparing(t -> t.getPoint().getId()))
                .collect(Collectors.toList());

        Long remainingAmount = amount;
        LocalDateTime now = LocalDateTime.now();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 정보 없음"));

        if (amount > order.getUsePointAmount()) {
            throw new IllegalArgumentException("포인트 사용 취소 요청 금액이 실제 사용 포인트보다 큼.");
        }

        for (PointTrack track : tracks) {
            if (remainingAmount == 0) break;

            Point point = track.getPoint();
            Long refundAmount = Math.min(remainingAmount, track.getAmount());

            if (point.getExpirationDate().isBefore(now)) {// 만료된 포인트 신규 적립 처리
                Point newPoint = new Point();
                newPoint.setUser(point.getUser());
                newPoint.setAmount(refundAmount);
                newPoint.setAvailableAmount(refundAmount);
                newPoint.setIsManual(false);
                newPoint.setCreatedDate(now);
                newPoint.setExpirationDate(now.plusDays(365));
                newPoint.setIsAvailable(true);
                pointRepository.save(newPoint);
            } else {
                point.setAvailableAmount(point.getAvailableAmount() + refundAmount);
                pointRepository.save(point);
            }

            PointTrack cancelTrack = new PointTrack();
            cancelTrack.setAmount(refundAmount);
            cancelTrack.setTrackDate(now);
            cancelTrack.setPoint(point);
            cancelTrack.setOrder(track.getOrder());
            cancelTrack.setPointType(PointType.USE_CANCEL);
            pointTrackRepository.save(cancelTrack);

            order.setUsePointAmount(order.getUsePointAmount() - refundAmount);
            orderRepository.save(order);

            remainingAmount -= refundAmount;
        }

    }

    // 포인트 적립 취소
    public void cancelDepositPoints(Long pointId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new RuntimeException("적립 취소 대상 ID가 아님"));

        LocalDateTime now = LocalDateTime.now();

        if (point.getExpirationDate().isBefore(now)) {
            throw new IllegalArgumentException("만료된 포인트는 적립 취소 불가");
        }

        // 사용된 금액이 있다면 취소할 수 없음
        if (point.getAvailableAmount() < point.getAmount()) {
            throw new IllegalArgumentException("적립 취소 불가");
        }

        PointTrack track = new PointTrack();
        track.setPoint(point);
        track.setAmount(point.getAmount());
        track.setTrackDate(now);
        track.setPointType(PointType.DEPOSIT_CANCEL);
        pointTrackRepository.save(track);

        point.setIsAvailable(false);
        point.setAvailableAmount(0L);
        pointRepository.save(point);
    }


    //사용자별 가용 포인트 조회
    public List<Point> getValidPointsByUser(User user) {
        LocalDateTime now = LocalDateTime.now();

        // 만료되지 않고 && 사용 가능한 포인트
        return pointRepository.findByUserAndAvailableAmountGreaterThan(user, 0L).stream()
                .filter(p -> p.getExpirationDate().isAfter(now) && p.getIsAvailable())
                .collect(Collectors.toList());
    }
}
