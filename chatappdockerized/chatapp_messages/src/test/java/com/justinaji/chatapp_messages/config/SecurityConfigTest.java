package com.justinaji.chatapp_messages.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void contextLoads() {
        // simply checking spring context loads without crashing
    }

    @Test
    void givenPublicEndpoint_whenGet_then200() throws Exception {
        mockMvc.perform(get("/chat.html"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void givenSecuredEndpointWithAuth_whenGet_then200() throws Exception {
        mockMvc.perform(get("/msg/gethistory"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void verifyJwtFilterInvoked() throws Exception {
        mockMvc.perform(get("/msg/gethistory"))
                .andExpect(status().isOk());

        verify(jwtFilter, atLeastOnce()).doFilter(any(), any(), any());
    }
}
