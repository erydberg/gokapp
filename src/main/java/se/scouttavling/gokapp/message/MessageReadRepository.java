package se.scouttavling.gokapp.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageReadRepository extends JpaRepository<MessageRead, Integer> {

    @Query("""
            select r from MessageRead r
            join fetch r.user
            where r.message.id = :messageId
            order by r.readAt asc
            """)
    List<MessageRead> findByMessageIdWithUser(@Param("messageId") Integer messageId);

    boolean existsByMessageIdAndUserId(Integer messageId, Long userId);

    long countByMessageId(Integer messageId);

    void deleteByUserId(Long userId);
}