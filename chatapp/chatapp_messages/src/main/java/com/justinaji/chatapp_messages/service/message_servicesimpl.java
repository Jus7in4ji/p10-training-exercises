package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.messageDTO;
import com.justinaji.chatapp_messages.exception.NoUserFound;
import com.justinaji.chatapp_messages.exception.No_messages;
import com.justinaji.chatapp_messages.exception.NotaMember;
import com.justinaji.chatapp_messages.exception.nochatFound;
import com.justinaji.chatapp_messages.dto.WSmessage;
import com.justinaji.chatapp_messages.model.chats;
import com.justinaji.chatapp_messages.model.members;
import com.justinaji.chatapp_messages.model.messages;
import com.justinaji.chatapp_messages.model.users;
import com.justinaji.chatapp_messages.repository.ChatRepo;
import com.justinaji.chatapp_messages.repository.MemberRepo;
import com.justinaji.chatapp_messages.repository.MessageRepo;
import com.justinaji.chatapp_messages.repository.UserRepo;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class message_servicesimpl implements mesage_services{

    @Autowired
    private WebClient webClient;

    private final UserRepo urepo;
    private final MemberRepo memberRepo;
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    public message_servicesimpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo, MessageRepo messageRepo) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
    }

    @Override
    public List<messageDTO> GetGrpmessages(String chatname) {

    users currentUser = CommonMethods.getCurrentUser();
    if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);
    List<messageDTO> dtoList = new ArrayList<>();

    if (chatRepo.existsByName(chatname)) { //find if chat of given name exists
        chats currentChat = chatRepo.findByName(chatname);
        List<members> chatMembers = memberRepo.findByChat(currentChat);

        members loggedInMember = chatMembers //Check if current user is part of this chat
                .stream()
                .filter(m -> m.getMember().equals(currentUser))
                .findFirst().orElse(null);

        if (loggedInMember == null) throw new NotaMember(currentUser.getName(), chatname);

        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(currentChat);

        chatMessages.forEach(msg -> {
            messageDTO dto = new messageDTO(
                CommonMethods.decryptMessage( msg.getMessage(), currentChat.getChat_key() ),
                msg.getSender().equals(currentUser)? msg.getSender().getName()+" (You)":msg.getSender().getName(),
                CommonMethods.formatTimestamp(msg.getSentTime())
            );
            dtoList.add(dto);
        });
    }
    
    else throw new nochatFound(chatname);
    if(dtoList.isEmpty()) throw new No_messages(chatname);

    return dtoList;
}

    @Override
    @Transactional
    public void SendGrpMessage(String chatname, String message){ 
        users currentUser = CommonMethods.getCurrentUser();
        if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);

        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (messageRepo.existsById(messageId));

        if(chatRepo.existsByName(chatname)){
            chats currentChat = chatRepo.findByName(chatname);
            List<members> chatMembers = memberRepo.findByChat(currentChat);

            members loggedInMember = chatMembers //Check if current user is part of this chat
                    .stream()
                    .filter(m -> m.getMember().equals(currentUser))
                    .findFirst()
                    .orElse(null);
            if (loggedInMember == null) throw new NotaMember(currentUser.getName(), chatname);

            String encryptedmessage = CommonMethods.encryptMessage(message, currentChat.getChat_key());

            messages newmsg = new messages(messageId, encryptedmessage, currentUser, currentChat, new Timestamp(System.currentTimeMillis()),false);  
            messageRepo.saveAndFlush(newmsg);
        }
        else throw new nochatFound(chatname);
    }

    @Override
    public List<messageDTO> GetPvtmessages(String chatname) {
        users currentUser = CommonMethods.getCurrentUser();
        if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);
        List<messageDTO> dtoList = new ArrayList<>();
        if (urepo.existsByName(chatname)) {

        users targetUser = urepo.findByName(chatname);

            Set<chats> myChatsSet = memberRepo.findByMember(currentUser)
                .stream()
                .map(m -> m.getChat())
                .collect(Collectors.toSet()); //chats with current user

            chats privatechat = memberRepo.findByMember(targetUser)
                .stream()
                .map(m->m.getChat())
                .filter(mchat->myChatsSet.contains(mchat))
                .filter(mchat-> !mchat.isIsgroup())
                .findFirst().orElse(null);
        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(privatechat);   
        
        chatMessages.forEach(msg -> {
                messageDTO dto = new messageDTO(
                    CommonMethods.decryptMessage(msg.getMessage(), privatechat.getChat_key()),
                    msg.getSender().equals(currentUser)? msg.getSender().getName()+" (You)":msg.getSender().getName() ,
                    CommonMethods.formatTimestamp(msg.getSentTime()) 
                );
                dtoList.add(dto);
            });
    }
    else throw new  NoUserFound(chatname);
    if(dtoList.isEmpty()) throw new No_messages("(Private) "+chatname);

    return dtoList;
    }

    public void settrueexcept(chats chat , users user){
        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(chat);
         // Loop through each message
        for (messages msg : chatMessages) {
            // If message sender is NOT the user passed
            if (!msg.getSender().equals(user)) {
                // Set msgread to true
                msg.setMsgread(true);
            }
        }

        // Save all updated messages in batch
        messageRepo.saveAll(chatMessages);
    }

    @Override
    @Transactional
    public void SendPvtMessage(String chatname, String message){
        users currentUser = CommonMethods.getCurrentUser();
        if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);

        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (messageRepo.existsById(messageId));

        if(urepo.existsByName(chatname)){
            users targetUser = urepo.findByName(chatname);

            Set<chats> myChatsSet = memberRepo.findByMember(currentUser)
                .stream()
                .map(m -> m.getChat())
                .collect(Collectors.toSet()); //chats with current user

            chats privatechat = memberRepo.findByMember(targetUser)
                .stream()
                .map(m->m.getChat())
                .filter(mchat->myChatsSet.contains(mchat))
                .filter(mchat-> !mchat.isIsgroup())
                .findFirst().orElse(null);

            String encryptedmessage = CommonMethods.encryptMessage(message, privatechat.getChat_key());

            messages newmsg = new messages(messageId, encryptedmessage, currentUser, privatechat, new Timestamp(System.currentTimeMillis()), false);
            messageRepo.saveAndFlush(newmsg);
        }
        else throw new NoUserFound(chatname);
    }

//---------------------------------------------------------------------------------------------------------------------------------------------------
//------------------------------------ Methods used by WEBSOCKET chats ------------------------------------------------------------------------------

    //dependent
    @Override
    public List<WSmessage> getchathistory(String username , String chatid, String timezone){
        if (username == null || username.trim().isEmpty())return null;
        List<WSmessage> history = new ArrayList<>();

         // CALL MS1 to get chat object
        Mono<chats> chatMono = webClient.get()
            .uri("http://localhost:8082/getchat?chatid=" + chatid)
            .retrieve()
            .bodyToMono(chats.class);

        chats targetchat = chatMono.block(); // convert reactive to normal object
        
        if (targetchat == null) {
            throw new RuntimeException("Chat ID not found!");
        }

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
        return messageId;
    }

    //independent / messages only 
    public void setread(String messageid){
        messages m = messageRepo.findById(messageid).orElseThrow(() -> new RuntimeException("message not found: "));
        m.setMsgread(true);
        messageRepo.save(m);
    }
}