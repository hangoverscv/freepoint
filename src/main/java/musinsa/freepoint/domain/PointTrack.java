package musinsa.freepoint.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PointTrack")
@Getter @Setter
@RequiredArgsConstructor
public class PointTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;
    private LocalDateTime trackDate;

    @Enumerated(EnumType.STRING)
    private PointType pointType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;

    @ManyToOne
    private Order order;



}
