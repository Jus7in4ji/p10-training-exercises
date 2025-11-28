package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.MessageRepo;

@Service
public class message_servicesimpl implements mesage_services{

    @Autowired
    private WebClient webClient;

    Logger logger = LoggerFactory.getLogger(message_servicesimpl.class);
    private final MessageRepo messageRepo;
    public message_servicesimpl( MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }


//---------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------ Methods used by WEBSOCKET chats ------------------------------------------------------------------------------

    //dependent
    @Override
    public List<WSmessage> getchathistory(String username , String chatid, String timezone){
        if (username == null || username.trim().isEmpty())return null;
        List<WSmessage> history = new ArrayList<>();

         // CALL MS1 to get chat object
        chats targetchat  = webClient.get().uri("http://localhost:8082/getchat?chatid=" + chatid).retrieve().bodyToMono(chats.class).block();

        if (targetchat == null) throw new RuntimeException("Chat ID not found!");


        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(targetchat);   
        
        chatMessages.forEach(msg -> {
                WSmessage dto = new WSmessage(
                    msg.getM_id(),
                    msg.getSender().getName() ,
                    CommonMethods.decryptMessage(msg.getMessage(), targetchat.getChat_key()),
                    CommonMethods.formatTimestamp(msg.getSentTime(), timezone),
                    null,
                    msg.isMsgread()
                );
                history.add(dto);
            });

        return history;
    }

    //dependent
    @Override
    public String Sendmessage(String text, String username, String chatid){
        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (messageRepo.existsById(messageId));

        users sender = webClient.get().uri("http://localhost:8082/getuser?username=" + username).retrieve().bodyToMono(users.class).block();
        if (sender == null) throw new RuntimeException("user not found!");

        chats chat  = webClient.get().uri("http://localhost:8082/getchat?chatid=" + chatid).retrieve().bodyToMono(chats.class).block();
        if (chat == null) throw new RuntimeException("Chat ID not found!");
        
        messages newmsg = new messages(messageId, CommonMethods.encryptMessage(text, chat.getChat_key()), sender, chat, Timestamp.from(Instant.now()), false);  
        messageRepo.save(newmsg);
        logger.info("message sent : "+username+" -->  chat("+chatid+"). ");

        return messageId;
    }

    //independent / messages only 
    public void setread(String messageid){
        messages m = messageRepo.findById(messageid).orElseThrow(() -> new RuntimeException("message not found: "));
        m.setMsgread(true);
        messageRepo.save(m);
    }
}