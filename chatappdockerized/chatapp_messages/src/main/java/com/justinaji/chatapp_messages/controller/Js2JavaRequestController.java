package com.justinaji.chatapp_messages.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.chatapp_messages.RestClientAPIs.MediaDetails;
import com.justinaji.chatapp_messages.RestClientAPIs.TempMsgs;
import com.justinaji.chatapp_messages.RestClientAPIs.Userchat;
import com.justinaji.chatapp_messages.dto.ChatsListObject;
import com.justinaji.chatapp_messages.dto.TempMsg;
import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.MessageRepo;
import com.justinaji.chatapp_messages.service.CommonMethods;
import com.justinaji.chatapp_messages.service.KafkaProducerServices;
import com.justinaji.chatapp_messages.service.MessageServicesImpl;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class Js2JavaRequestController {
    
    Logger logger = LoggerFactory.getLogger(Js2JavaRequestController.class);

    private final TempMsgs tempMsgMS;
    private final Userchat userchat;
    private final MediaDetails mediaDetails;
    private final MessageRepo messageRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServicesImpl msgservice;
    private final KafkaProducerServices kafkaProducerServices;

    public Js2JavaRequestController(
        TempMsgs tempMsgMS,
        Userchat userchat,
        MediaDetails mediaDetails,
        MessageServicesImpl msgservice,
        MessageRepo messageRepo, 
        KafkaProducerServices kafkaProducerServices,
        SimpMessagingTemplate messagingTemplate){
            this.tempMsgMS = tempMsgMS;
            this.userchat = userchat;
            this.mediaDetails = mediaDetails;
            this.messageRepo = messageRepo;
            this.msgservice = msgservice;
            this.messagingTemplate = messagingTemplate;
            this.kafkaProducerServices = kafkaProducerServices;
        }
   
    @PostMapping("/msg/gethistory")
    public List<WSmessage> RetrieveChatHistory(@RequestBody Map<String, String> payload) {   
        return msgservice.getchathistory(payload.get("user"), payload.get("chatid"), payload.get("timezone"));
    }

    @PostMapping("/temp/sendtemp")
    public void StoreinH2DB(@RequestBody TempMsg temp) {
        kafkaProducerServices.StoreTempmsg(temp);
    }
    
    @GetMapping("/msg/gettemp")
    public Map<String, String> GetValidTempMsgs(@RequestParam String sendername) {
        HashMap<String, String> result = new HashMap<>();
        String details = "Messages retrieved and sent successfully";
        try {
            //retrieve messages from in memory db 
            List<TempMsg> temps = tempMsgMS.getTempMessages(sendername);
            if (temps == null) throw new RuntimeException("Temp messages not found!");

            DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            temps.forEach(msg->{
                String room , text, TS;
                text = msg.getMessage();
                room = msg.getChatid();
                
                TS = msg.getFormattedtime().substring(0, 21).replace("T", " ");
                LocalDateTime ldt = LocalDateTime.parse(TS,dbFormat);
                //convert timestamp to displayable string
                TS = CommonMethods.formatTimestamp(Timestamp.valueOf(ldt));

                WSmessage wsm = new WSmessage(null, sendername, text, TS, room, false, false);
                //add to db
                wsm.setMsgid(msgservice.Sendmessage(text, sendername, room, Timestamp.valueOf(ldt)));
                //send via websocket
                messagingTemplate.convertAndSend("/topic/" + room, wsm);
            }); 
        } catch (Exception e) {
            details = e.getMessage();
        }
        result.put("Status","Success");
        result.put("Details",details);
        return result;
    }

    @GetMapping("/msg/availablechats")
    public List<ChatsListObject> getMethodName(@RequestParam String username){
        List<ChatsListObject> listchats = new ArrayList<>();
        try {
            List<chats> chats = userchat.getAvailableChats(username);
            users current_user  = userchat.getUser(username);  
            chats.forEach(chat->{
                List<messages> messages =  messageRepo.findByChat(chat);
                boolean unread = messages.stream().anyMatch(
                    message -> !message.isMsgread() && !message.getSender().equals(current_user));
                    // check for unread messages by other users
                if (!unread){
                    try {
                        List<media> chatmedia = mediaDetails.getMedia(chat.getChatId());
                        unread = chatmedia.stream().anyMatch(
                            media-> !media.isMsgread() && !media.getSender().equals(current_user.getName())); 
                    } 
                    catch (Exception e) { logger.error("Error occured while fetching media: "+e); }
                }
                ChatsListObject c = new ChatsListObject(chat.getChatId(),chat.getName(),chat.isIsgroup(),unread);
                listchats.add(c);
            });
        } 
        catch (Exception e) { logger.error("Error occured while fetching Available chats: "+e); }
        return listchats;
    }
    
}
