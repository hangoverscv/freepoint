package musinsa.freepoint.repository;

import musinsa.freepoint.domain.Point;
import musinsa.freepoint.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByUser(User user);

    // 특정 사용자의 포인트를 조회 (사용가능한 포인트만)
    List<Point> findByUserAndAvailableAmountGreaterThan(User user, Long amount);

}
