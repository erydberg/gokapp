package se.scouttavling.gokapp.message;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usermessage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Fyll i en rubrik")
    @Column(name = "title", length = 100)
    private String title;

    @NotEmpty(message = "Fyll i ett meddelande")
    @Column(name = "body", length = 2000)
    private String body;

    @Column(name = "active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "createdby", length = 100)
    private String createdBy;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MessageRead> reads = new HashSet<>();
}