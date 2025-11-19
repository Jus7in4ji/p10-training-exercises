package com.justinaji.jproj.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.exception.NoUserFound;
import com.justinaji.jproj.exception.No_messages;
import com.justinaji.jproj.exception.NotaMember;
import com.justinaji.jproj.exception.nochatFound;
import com.justinaji.jproj.model.Logs;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.members;
import com.justinaji.jproj.model.messages;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.ChatRepo;
import com.justinaji.jproj.repository.LogRepo;
import com.justinaji.jproj.repository.MemberRepo;
import com.justinaji.jproj.repository.MessageRepo;
import com.justinaji.jproj.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class message_servicesimpl implements mesage_services{

    private final UserRepo urepo;
    private final MemberRepo memberRepo;
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    private final LogRepo logRepo;
    public message_servicesimpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo, MessageRepo messageRepo, LogRepo logRepo) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
        this.logRepo = logRepo;
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

            messages newmsg = new messages(messageId, encryptedmessage, currentUser, currentChat, new Timestamp(System.currentTimeMillis()));  
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

            messages newmsg = new messages(messageId, encryptedmessage, currentUser, privatechat, new Timestamp(System.currentTimeMillis()));
            messageRepo.saveAndFlush(newmsg);
        }
        else throw new NoUserFound(chatname);
    }

    public HashMap<String,String> ischatvalid(String chatname, String username, boolean isgroup){
        HashMap<String , String> result = new HashMap<>();
        result.put("roomid", "public");

        if (isgroup){
            if(chatRepo.existsByName(chatname)){
                chats currentChat = chatRepo.findByName(chatname);
                List<members> chatMembers = memberRepo.findByChat(currentChat);

                members loggedInMember = chatMembers //Check if current user is part of this chat
                        .stream()
                        .filter(m -> m.getMember().getName().equals(username))
                        .findFirst()
                        .orElse(null);
                        
                if (loggedInMember == null) result.put("Status",  username+" is not a member of "+chatname+".");
                else{
                    result.put("Status",  "Success");
                    result.put("roomid", currentChat.getC_id());
                }
            }
            else result.put("Status","No Chat of name "+chatname+" exists.");
        }
        else{
            if(urepo.existsByName(chatname)){
                if(chatname.equals(username)) result.put("Status","Make sure the username isn't your own.");
                
                else{
                    users targetUser = urepo.findByName(chatname);
                    Set<chats> myChatsSet = memberRepo.findByMember(urepo.findByName(username))
                        .stream()
                        .map(m -> m.getChat())
                        .collect(Collectors.toSet()); //chats with current user

                    chats privatechat = memberRepo.findByMember(targetUser)
                        .stream()
                        .map(m->m.getChat())
                        .filter(mchat->myChatsSet.contains(mchat))
                        .filter(mchat-> !mchat.isIsgroup())
                        .findFirst().orElse(null); 
                    result.put("Status",  "Success");
                    result.put("roomid", privatechat.getC_id());
                    }
            }
            else result.put("Status","No User of name "+chatname+" exists.");
        }
        return result;
    }
}