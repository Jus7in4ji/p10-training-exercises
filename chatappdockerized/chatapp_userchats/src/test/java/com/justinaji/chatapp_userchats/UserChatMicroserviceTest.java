package com.justinaji.chatapp_userchats;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

@SpringBootTest
class UserChatMicroserviceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verify the application context loads successfully
        assertNotNull(applicationContext);
    }

    @Test
    void schedulingIsEnabled() {
        //checking if the scheduler bean exists
        assertNotNull(applicationContext.getBean(ScheduledAnnotationBeanPostProcessor.class));
    }
}