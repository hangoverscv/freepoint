package musinsa.freepoint.service;

import musinsa.freepoint.domain.Point;
import musinsa.freepoint.domain.User;
import musinsa.freepoint.repository.PointRepository;
import musinsa.freepoint.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceV1 implements UserService{


    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    public UserServiceV1(UserRepository userRepository, PointRepository pointRepository) {
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
    }


    @Override
    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void join(String username, Long maxPoints) {
        User user = new User();
        user.setUsername(username);
        user.setMaxPoints(maxPoints);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void updateUserMaxPoints(Long userId, Long newMaxPoints) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자가 없습니다."));

        // 현재 보유 포인트 합산
        LocalDateTime now = LocalDateTime.now();
        Long currentAvailablePoints = pointRepository.findByUser(user)
                .stream()
                .filter(p -> p.getExpirationDate().isAfter(now) && p.getIsAvailable())
                .mapToLong(Point::getAvailableAmount)
                .sum();

        if (newMaxPoints < currentAvailablePoints) {
            throw new IllegalArgumentException("현재 보유 중인 포인트보다 적게 설정할 수 없습니다.");
        }

        user.setMaxPoints(newMaxPoints);
        userRepository.save(user);
    }

}
