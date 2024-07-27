// package com.cmpt213.finalProject.SYNC.repository;

// import com.cmpt213.finalProject.SYNC.models.ChatMessage;
// import com.cmpt213.finalProject.SYNC.models.ChatMessageKey;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface MessageRepository extends JpaRepository<ChatMessage, ChatMessageKey> {

//     @Query("SELECT m FROM ChatMessage m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.timestamp ASC")
//     List<ChatMessage> findMessagesBetweenUsers(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

//     List<ChatMessage> findTop10ByReceiverIdOrderByTimestampDesc(Integer receiverId);

//     List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Integer senderId, Integer receiverId);
// }