package musinsa.freepoint.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "points")
@Getter @Setter
@NoArgsConstructor
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    private Boolean isManual;

    private Long amount;
    @Schema(hidden = true)
    private Long availableAmount;

    private LocalDateTime expirationDate;

    private LocalDateTime createdDate;

    private String orderNumber;

    private Boolean isAvailable;

    @Schema(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
