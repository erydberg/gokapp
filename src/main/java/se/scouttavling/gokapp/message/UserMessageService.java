package se.scouttavling.gokapp.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.scouttavling.gokapp.security.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMessageService {

    private final UserMessageRepository userMessageRepository;
    private final MessageReadRepository messageReadRepository;

    public List<UserMessage> getAll() {
        return userMessageRepository.findAllByOrderByCreatedDesc();
    }

    public Optional<UserMessage> getById(Integer id) {
        return userMessageRepository.findById(id);
    }

    /**
     * On a new message the creation data is stamped. On an edit the row is loaded from the
     * database and only the editable fields are copied over, so that the read log and the
     * original creation data survive.
     */
    public UserMessage save(UserMessage message, String username) {

        if (message.getId() == null) {
            message.setCreated(LocalDateTime.now());
            message.setCreatedBy(username);
            return userMessageRepository.save(message);
        }

        UserMessage messageFromDb = userMessageRepository.findById(message.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + message.getId()));
        messageFromDb.setTitle(message.getTitle());
        messageFromDb.setBody(message.getBody());
        messageFromDb.setActive(message.isActive());

        return userMessageRepository.save(messageFromDb);
    }

    public void delete(Integer id) {
        userMessageRepository.deleteById(id);
    }

    public List<UserMessage> getUnreadFor(User user) {
        return userMessageRepository.findUnreadForUser(user.getId());
    }

    public void markAsRead(Integer messageId, User user) {

        if (messageReadRepository.existsByMessageIdAndUserId(messageId, user.getId())) {
            return;
        }

        Optional<UserMessage> message = userMessageRepository.findById(messageId);
        if (message.isEmpty()) {
            // the message was removed while the user had it open - nothing to log
            return;
        }

        MessageRead messageRead = MessageRead.builder()
                .message(message.get())
                .user(user)
                .readAt(LocalDateTime.now())
                .build();
        messageReadRepository.save(messageRead);
    }

    public List<MessageRead> getReadersFor(Integer messageId) {
        return messageReadRepository.findByMessageIdWithUser(messageId);
    }

    public long countReadersFor(Integer messageId) {
        return messageReadRepository.countByMessageId(messageId);
    }
}