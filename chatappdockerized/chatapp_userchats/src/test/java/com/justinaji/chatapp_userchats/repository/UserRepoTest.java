package com.justinaji.chatapp_userchats.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.justinaji.chatapp_userchats.model.users;

@DataJpaTest
class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    private users user1;
    private users user2;
    private users inactiveUser;

    @BeforeEach
    void setup() {
        user1 = new users("u1", "mail1@domain.com", "justin", "pass", true, LocalDateTime.now());
        user2 = new users("u2", "mail2@domain.com", "alex", "pass", true, LocalDateTime.now().minusMinutes(40));
        inactiveUser = new users("u3", "mail3@domain.com", "kannan", "pass", false, null);

        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(inactiveUser);
    }

    @Test
    void ExistsByEmail() {
        assertThat(userRepo.existsByEmail("mail1@domain.com")).isTrue();
        assertThat(userRepo.existsByEmail("unknown@mail.com")).isFalse();
    }

    @Test
    void ExistsByName() {
        assertThat(userRepo.existsByName("justin")).isTrue();
        assertThat(userRepo.existsByName("nobody")).isFalse();
    }

    @Test
    void FindByEmail() {
        users found = userRepo.findByEmail("mail2@domain.com");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("alex");
    }

    @Test
    void FindByName() {
        users found = userRepo.findByName("kannan");
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("mail3@domain.com");
    }

    @Test
    void FindByStatusTrueAndRecentLoginBefore() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

        List<users> result = userRepo.findByStatusTrueAndRecentLoginBefore(cutoff);

        // Only user2 matches: active + last login 40m ago
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getU_id()).isEqualTo("u2");
    }
}
