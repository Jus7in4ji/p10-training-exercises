package com.justinaji.chatapp_userchats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justinaji.chatapp_userchats.model.members;
import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.model.MemberId;
import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<members, MemberId> {
    List<members> findByChat(chats chat);
    List<members> findByMember(users member);
}
