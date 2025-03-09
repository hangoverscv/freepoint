package musinsa.freepoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import musinsa.freepoint.domain.User;

@Getter @Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}
