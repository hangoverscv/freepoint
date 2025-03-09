package musinsa.freepoint.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long maxPoints;

    @OneToMany(mappedBy = "user")
    private List<Point> points;

    public static User of(Long id) {
        User user = new User();
        user.id = id;
        return user;
    }
}
