package se.scouttavling.gokapp.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {

    List<UserMessage> findAllByOrderByCreatedDesc();

    @Query("""
            select m from UserMessage m
            where m.active = true
              and m.id not in (select r.message.id from MessageRead r where r.user.id = :userId)
            order by m.created asc
            """)
    List<UserMessage> findUnreadForUser(@Param("userId") Long userId);
}