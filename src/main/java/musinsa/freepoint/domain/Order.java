package musinsa.freepoint.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String orderNumber;
    private Long orderAmount;
    private Long usePointAmount;
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointTrack> pointTrack;

    @ManyToOne
    private User user;

    @PrePersist
    public void generateOrderNumber() {
        this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
