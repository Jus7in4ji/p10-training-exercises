package com.justinaji.chatapp_userchats.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.justinaji.chatapp_userchats.model.chats;

@DataJpaTest  // loads only JPA components + H2 DB
class ChatRepoTest {

    @Autowired
    private ChatRepo chatRepo;

    @Test
    void ExistsByName_whenChatExists() {
        chats chat = new chats("c1", "GeneralGroup", null, true, "key123");
        chatRepo.save(chat);

        boolean exists = chatRepo.existsByName("GeneralGroup");

        assertThat(exists).isTrue();
    }

    @Test
    void ExistsByName_whenChatDoesNotExist() {
        boolean exists = chatRepo.existsByName("UnknownChat");

        assertThat(exists).isFalse();
    }

    @Test
    void FindByName_returnsCorrectChat() {
        chats chat = new chats("c2", "Friends", null, true, "key999");
        chatRepo.save(chat);

        chats result = chatRepo.findByName("Friends");

        assertThat(result).isNotNull();
        assertThat(result.getC_id()).isEqualTo("c2");
        assertThat(result.getName()).isEqualTo("Friends");
    }
}
