package musinsa.freepoint.service;

import musinsa.freepoint.domain.User;

import java.util.Optional;

public interface UserService {

    //회원가입
    void join(String username, Long maxPoints);

    //회원삭제
    void delete(Long id);

    //조회
    Optional<User> findUser(Long id);

    void updateUserMaxPoints(Long userId, Long newMaxPoints);
}
