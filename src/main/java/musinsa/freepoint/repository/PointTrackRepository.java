package musinsa.freepoint.repository;

import musinsa.freepoint.domain.PointTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTrackRepository extends JpaRepository<PointTrack, Long> {

    List<PointTrack> findByOrderId(Long orderId);


}
