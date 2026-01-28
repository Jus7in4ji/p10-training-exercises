package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.MessageRepo;

@Service
public class MessageServicesImpl implements MesageServices{

    Logger logger = LoggerFactory.getLogger(MessageServicesImpl.class);
    
    private final KafkaProducerServices kafkaProducerServices;
    private final WebClient userChatsWebClient;
    private final WebClient MediaWebClient;
    private final MessageRepo messageRepo;
    private final RestClientTest restClientTest;

    public MessageServicesImpl( 
        @Qualifier("userChatsWebClient") WebClient userChatsWebClient,
        @Qualifier("MediaWebClient") WebClient MediaWebClient,
        KafkaProducerServices kafkaProducerServices,
        RestClientTest restClientTest, 
        MessageRepo messageRepo) {
            this.kafkaProducerServices = kafkaProducerServices;
            this.userChatsWebClient = userChatsWebClient;
            this.MediaWebClient = MediaWebClient;
            this.restClientTest = restClientTest;
            this.messageRepo = messageRepo;
        }

    @Override
    public List<WSmessage> getchathistory(String username , String chatid, String timezone){
        if (username == null || username.trim().isEmpty())return null;
        List<WSmessage> history = new ArrayList<>();

        chats targetchat = restClientTest.getChat(chatid);
        if (targetchat == null) throw new RuntimeException("Chat not found!");

        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(targetchat);   
        
        chatMessages.forEach(msg -> {
            WSmessage dto = new WSmessage(
                msg.getM_id(),
                msg.getSender().getName() ,
                CommonMethods.decryptMessage(msg.getMessage(), targetchat.getChat_key()),
                msg.getSentTime().toString(),
                null,
                msg.isMsgread(),
                false
            );
            history.add(dto);
        });
        try {
            List<media> chatMedia = MediaWebClient.get()
                .uri("/media/getchatmedia?chatid=" + chatid)
                .retrieve()
                .bodyToFlux(media.class)
                .collectList().block();
            if (chatMedia == null) throw new RuntimeException("files not found!");

            chatMedia.forEach(file->{
                WSmessage dto = new WSmessage(
                    file.getFileid(),
                    file.getSender() ,
                    file.getName(),
                    file.getSenttime().substring(0, 21).replace("T", " "),
                    null,
                    file.isMsgread(),
                    true
                );
                history.add(dto);
            });
        } catch (Exception e) {
        }

        history.sort(Comparator.comparing(WSmessage::getSentTime));

        DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        history.forEach(m -> {
            LocalDateTime ldt = LocalDateTime.parse(m.getSentTime(),dbFormat);
            m.setSentTime(CommonMethods.formatTimestamp(Timestamp.valueOf(ldt), timezone));
        });

        return history;
    }

    @Override
    public String Sendmessage(String text, String username, String chatid,Timestamp ts){
        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } // unique chat id
        while (messageRepo.existsById(messageId));

        users sender = restClientTest.getUser(username);
        if (sender == null) throw new RuntimeException("user not found!");

        chats chat = restClientTest.getChat(chatid);        
        if (chat == null) throw new RuntimeException("Chat ID not found!");
        
        if (ts == null)ts =  Timestamp.from(Instant.now());
        messages newmsg = new messages(messageId, CommonMethods.encryptMessage(text, chat.getChat_key()), sender, chat, ts, false);  
        messageRepo.save(newmsg);

        logger.info("message sent : "+username+" -->  chat("+chatid+"). ");
        return messageId;
    }

    public void setread(String messageid){
        if (messageRepo.existsById(messageid)){
            messages m = messageRepo.findById(messageid).orElseThrow(() -> new RuntimeException("message not found: "));
            m.setMsgread(true);
            messageRepo.save(m);
        }
        else kafkaProducerServices.SetFileRead(messageid);
        
    }
}