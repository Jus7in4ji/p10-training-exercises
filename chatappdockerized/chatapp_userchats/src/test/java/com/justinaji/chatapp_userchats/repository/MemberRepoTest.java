package com.justinaji.chatapp_userchats.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.members;
import com.justinaji.chatapp_userchats.model.users;

@DataJpaTest
class MemberRepoTest {

    @Autowired
    private MemberRepo memberRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private UserRepo userRepo;

    private chats chat1;
    private users user1;
    private users user2;

    @BeforeEach
    void setup() {
        // Create users
        user1 = new users("u1", "mail1", "user1", "pass", false, null);
        user2 = new users("u2", "mail2", "user2", "pass", false, null);
        userRepo.save(user1);
        userRepo.save(user2);

        // Create chat
        chat1 = new chats("c1", "GroupA", user1, true, "key");
        chatRepo.save(chat1);

        // Members
        members m1 = new members(chat1, user1, true);
        members m2 = new members(chat1, user2, false);

        memberRepo.save(m1);
        memberRepo.save(m2);
    }

    @Test
    void FindByChat() {
        List<members> result = memberRepo.findByChat(chat1);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("member").containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void FindByMember_user1() {
        List<members> result = memberRepo.findByMember(user1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChat().getC_id()).isEqualTo("c1");
    }

    @Test
    void FindByMember_user2() {
        List<members> result = memberRepo.findByMember(user2);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChat().getC_id()).isEqualTo("c1");
    }
}
