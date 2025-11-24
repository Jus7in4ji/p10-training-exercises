package com.justinaji.chatapp_messages.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justinaji.chatapp_messages.model.members;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.model.MemberId;
import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<members, MemberId> {
    List<members> findByChat(chats chat);
    List<members> findByMember(users member);
}
