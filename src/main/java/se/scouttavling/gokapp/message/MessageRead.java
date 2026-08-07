package se.scouttavling.gokapp.message;

import jakarta.persistence.*;
import lombok.*;
import se.scouttavling.gokapp.security.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_read",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_message_read",
                        columnNames = {"message_id", "user_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private UserMessage message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "readat")
    private LocalDateTime readAt;
}