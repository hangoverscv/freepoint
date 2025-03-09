package musinsa.freepoint.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import musinsa.freepoint.domain.User;
import musinsa.freepoint.dto.UserDto;
import musinsa.freepoint.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Getter @Setter
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 관련 API")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @Operation(summary = "사용자 가입")
    @PostMapping("")
    public void join(@RequestParam String username, @RequestParam Long maxPoints){
        userService.join(username, maxPoints);
    }

    @Operation(summary = "사용자 조회")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id){
        User user = userService.findUser(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.delete(id);
    }

    @Operation(summary = "사용자 최대 보유 포인트 수정")
    @PutMapping("/{userId}/max-points")
    public void updateUserMaxPoints(@PathVariable Long userId,
                                    @RequestParam Long newMaxPoints) {
        userService.updateUserMaxPoints(userId, newMaxPoints);
    }

}
