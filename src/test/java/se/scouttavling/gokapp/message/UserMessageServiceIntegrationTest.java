package se.scouttavling.gokapp.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.scouttavling.gokapp.security.Role;
import se.scouttavling.gokapp.security.User;
import se.scouttavling.gokapp.security.UserRepository;
import se.scouttavling.gokapp.security.UserService;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@ActiveProfiles("test")
class UserMessageServiceIntegrationTest {

    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private MessageReadRepository messageReadRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User kontrollant;

    @BeforeEach
    void setUp() {
        messageReadRepository.deleteAll();
        userMessageRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("k1");
        user.setPassword("losen_test");
        user.addRole(Role.ROLE_USER);
        user.setEnabled(true);
        kontrollant = userService.save(user);
    }

    private UserMessage createMessage(String title, boolean active) {
        UserMessage message = new UserMessage();
        message.setTitle(title);
        message.setBody("Ett meddelande");
        message.setActive(active);
        return userMessageService.save(message, "admin");
    }

    @Test
    void anActiveMessageIsUnreadUntilItIsAcknowledged() {
        UserMessage message = createMessage("Kontroll 4 är flyttad", true);
        assertThat(message.getCreated()).isNotNull();
        assertThat(message.getCreatedBy()).isEqualTo("admin");

        assertThat(userMessageService.getUnreadFor(kontrollant))
                .extracting(UserMessage::getTitle)
                .containsExactly("Kontroll 4 är flyttad");

        userMessageService.markAsRead(message.getId(), kontrollant);

        assertThat(userMessageService.getUnreadFor(kontrollant)).isEmpty();

        List<MessageRead> readers = userMessageService.getReadersFor(message.getId());
        assertThat(readers).hasSize(1);
        assertThat(readers.get(0).getUser().getUsername()).isEqualTo("k1");
        assertThat(readers.get(0).getReadAt()).isNotNull();
    }

    @Test
    void acknowledgingTwiceOnlyGivesOneLogEntry() {
        UserMessage message = createMessage("Sista start 14:00", true);

        userMessageService.markAsRead(message.getId(), kontrollant);
        userMessageService.markAsRead(message.getId(), kontrollant);

        assertThat(userMessageService.countReadersFor(message.getId())).isEqualTo(1);
    }

    @Test
    void anInactiveMessageIsNeverShown() {
        createMessage("Avaktiverat", false);

        assertThat(userMessageService.getUnreadFor(kontrollant)).isEmpty();
    }

    @Test
    void editingAMessageKeepsTheReadLog() {
        UserMessage message = createMessage("Ursprunglig rubrik", true);
        userMessageService.markAsRead(message.getId(), kontrollant);

        UserMessage edited = new UserMessage();
        edited.setId(message.getId());
        edited.setTitle("Ändrad rubrik");
        edited.setBody("Ändrad text");
        edited.setActive(true);
        UserMessage saved = userMessageService.save(edited, "admin");

        assertThat(saved.getTitle()).isEqualTo("Ändrad rubrik");
        assertThat(saved.getCreated()).isCloseTo(message.getCreated(), within(1, ChronoUnit.SECONDS));
        assertThat(saved.getCreatedBy()).isEqualTo("admin");
        assertThat(userMessageService.countReadersFor(message.getId())).isEqualTo(1);
        assertThat(userMessageService.getUnreadFor(kontrollant)).isEmpty();
    }

    @Test
    void deletingAMessageAlsoRemovesTheReadLog() {
        UserMessage message = createMessage("Tas bort", true);
        userMessageService.markAsRead(message.getId(), kontrollant);
        assertThat(messageReadRepository.count()).isEqualTo(1);

        userMessageService.delete(message.getId());

        assertThat(userMessageRepository.count()).isZero();
        assertThat(messageReadRepository.count()).isZero();
    }

    @Test
    void deletingAUserWithAcknowledgedMessagesWorks() {
        UserMessage message = createMessage("Kvar efter borttagen användare", true);
        userMessageService.markAsRead(message.getId(), kontrollant);

        userService.deleteUser(kontrollant.getId());

        assertThat(userRepository.count()).isZero();
        assertThat(messageReadRepository.count()).isZero();
        assertThat(userMessageRepository.count()).isEqualTo(1);
    }
}