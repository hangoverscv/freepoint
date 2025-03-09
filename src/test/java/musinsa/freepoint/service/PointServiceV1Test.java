package musinsa.freepoint.service;

import musinsa.freepoint.domain.Point;
import musinsa.freepoint.domain.PointTrack;
import musinsa.freepoint.domain.User;
import musinsa.freepoint.repository.PointRepository;
import musinsa.freepoint.repository.PointTrackRepository;
import musinsa.freepoint.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PointServiceV1Test {
    @Mock
    private PointRepository pointRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointTrackRepository pointTrackRepository;

    @InjectMocks
    private PointServiceV1 pointService;

    private User user;
    private Point point;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setMaxPoints(100000L);

        point = new Point();
        point.setId(1L);
        point.setAmount(5000L);
        point.setAvailableAmount(5000L);
        point.setCreatedDate(LocalDateTime.now());
        point.setExpirationDate(LocalDateTime.now().plusDays(365));
        point.setIsManual(false);
        point.setIsAvailable(true);
        point.setUser(user);
    }

    @Test
    void 포인트적립() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointRepository.save(any(Point.class))).thenReturn(point);

        assertDoesNotThrow(() -> pointService.depositPoints(point, 1L));

        verify(pointRepository, times(1)).save(any(Point.class));
        verify(pointTrackRepository, times(1)).save(any(PointTrack.class));
    }

    @Test
    void 사용자유무체크() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> pointService.usePoints(point, 1L));
        assertEquals("사용자가 없습니다.", exception.getMessage());
    }

    @Test
    void 적립취소_포인트검증() {
        when(pointRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> pointService.cancelDepositPoints(1L));
        assertEquals("적립 취소 대상 ID가 아님", exception.getMessage());
    }

    @Test
    void 적립취소_만료된포인트() {
        point.setExpirationDate(LocalDateTime.now().minusDays(1));
        when(pointRepository.findById(1L)).thenReturn(Optional.of(point));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> pointService.cancelDepositPoints(1L));
        assertEquals("만료된 포인트는 적립 취소 불가", exception.getMessage());
    }
}