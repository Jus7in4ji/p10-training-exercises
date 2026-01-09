package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.MediaRepo;
import com.justinaji.chatapp_messages.repository.MessageRepo;

@Service
public class MessageServicesImpl implements MesageServices{

    Logger logger = LoggerFactory.getLogger(MessageServicesImpl.class);

    private final WebClient userChatsWebClient;
    private final WebClient MediaWebClient;
    private final MessageRepo messageRepo;
    private final MediaRepo mediaRepo;
    public MessageServicesImpl( 
        @Qualifier("userChatsWebClient") WebClient userChatsWebClient,
        @Qualifier("MediaWebClient") WebClient MediaWebClient, 
        MessageRepo messageRepo, 
        MediaRepo mediaRepo) {
            this.userChatsWebClient = userChatsWebClient;
            this.MediaWebClient = MediaWebClient;
            this.messageRepo = messageRepo;
            this.mediaRepo = mediaRepo;
        }

    @Override
    public List<WSmessage> getchathistory(String username , String chatid, String timezone){
        if (username == null || username.trim().isEmpty())return null;
        List<WSmessage> history = new ArrayList<>();

        chats targetchat = userChatsWebClient.get()
            .uri("/userchat/getchat?chatid=" + chatid) 
            .retrieve()
            .bodyToMono(chats.class)
            .block();
        if (targetchat == null) throw new RuntimeException("Chat ID not found!");

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
        
        List<media> chatMedia = MediaWebClient.get()
            .uri("/media/getchatmedia?chatid=" + chatid)
            .retrieve()
            .bodyToFlux(media.class)
            .collectList()
            .block();
        if (chatMedia == null) throw new RuntimeException("Chat ID not found!");

        chatMedia.forEach(file->{
            WSmessage dto = new WSmessage(
                    file.getFileid(),
                    file.getSender() ,
                    file.getName(),
                    file.getSenttime().toString(),
                    null,
                    file.isMsgread(),
                    true
                );
                history.add(dto);
        });

        history.sort(Comparator.comparing(WSmessage::getSentTime));

        DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        history.forEach(m -> {
            LocalDateTime ldt = parseDbTimestamp(m.getSentTime());

            m.setSentTime(
                CommonMethods.formatTimestamp(
                    Timestamp.valueOf(ldt),
                    timezone
                )
            );
        });

        return history;
    }

    @Override
    public String Sendmessage(String text, String username, String chatid){
        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } // unique chat id
        while (messageRepo.existsById(messageId));

        users sender = userChatsWebClient.get()
            .uri("/userchat/getuser?username=" + username)
            .retrieve()
            .bodyToMono(users.class)
            .block();
        if (sender == null) throw new RuntimeException("user not found!");

        chats chat = userChatsWebClient.get()
            .uri("/userchat/getchat?chatid=" + chatid)  
            .retrieve()
            .bodyToMono(chats.class)
            .block();        
        if (chat == null) throw new RuntimeException("Chat ID not found!");
        
        messages newmsg = new messages(messageId, CommonMethods.encryptMessage(text, chat.getChat_key()), sender, chat, Timestamp.from(Instant.now()), false);  
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
        else{
            media m = mediaRepo.findById(messageid).orElseThrow(() -> new RuntimeException("message not found: "));
            m.setMsgread(true);
            SetFileRead(messageid);
            mediaRepo.save(m);
        }
    }

    @Autowired
    private KafkaTemplate<String,Object> template;

    public void SetFileRead(String fileid){
        CompletableFuture<SendResult<String, Object>> future = template.send("fileread", fileid);
        future.whenComplete((result,ex)->{
            if (ex ==null) logger.info(
                "Sent Message [ "+ fileid+
                "] with offset ["+result.getRecordMetadata().offset()+ 
                "] to partition [" + result.getRecordMetadata().partition()+"]");
            
            else System.out.println("Unable to Set to read ("+fileid+") due to : "+ex.getMessage());
            
        });
    }

private static final DateTimeFormatter LEGACY_TS_FORMATTER =
        new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
                .optionalEnd()
                .toFormatter();

private LocalDateTime parseDbTimestamp(String ts) {
    try {
        if (ts.contains("T") && (ts.contains("+") || ts.endsWith("Z"))) {
            return OffsetDateTime.parse(ts).toLocalDateTime();
        }
        if (ts.contains("T")) {
            return LocalDateTime.parse(ts);
        }

        return LocalDateTime.parse(ts, LEGACY_TS_FORMATTER);

    } catch (Exception e) {
        throw new RuntimeException("Unable to parse timestamp: " + ts, e);
    }
}





}