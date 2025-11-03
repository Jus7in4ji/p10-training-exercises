package com.justinaji.jproj.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.justinaji.jproj.model.members;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.model.MemberId;
import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<members, MemberId> {
    List<members> findByChat(chats chat);
    List<members> findByMember(users member);
}
