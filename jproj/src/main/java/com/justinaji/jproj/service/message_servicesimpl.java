package com.justinaji.jproj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.justinaji.jproj.dto.messageDTO;
import com.justinaji.jproj.exception.No_messages;
import com.justinaji.jproj.exception.nochatFound;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.members;
import com.justinaji.jproj.model.messages;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.ChatRepo;
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
    public message_servicesimpl(UserRepo urepo, MemberRepo memberRepo, ChatRepo chatRepo, MessageRepo messageRepo) {
        this.urepo = urepo; 
        this.memberRepo = memberRepo;
        this.chatRepo = chatRepo;
        this.messageRepo = messageRepo;
    }

    @Override
    public List<messageDTO> Getmessages(String chatname) {

    users currentUser = CommonMethods.getCurrentUser();
    if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);
    List<messageDTO> dtoList = new ArrayList<>();

    // Chat name exists → directly fetch messages
    if (chatRepo.existsByName(chatname)) {

        chats currentChat = chatRepo.findByName(chatname);
        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(currentChat);

        chatMessages.forEach(msg -> {
            messageDTO dto = new messageDTO(
                msg.getSender().getName(),
                msg.getMessage(),
                msg.getSentTime()
            );
            dtoList.add(dto);
        });
    }

    // Otherwise treat chatname as a username
    else if (urepo.existsByName(chatname)) {

        users targetUser = urepo.findByName(chatname);

        List<members> myChats = memberRepo.findByMember(currentUser); //chats with current user
        List<members> targetchats = memberRepo.findByMember(targetUser); //chats with target user

        Set<chats> myChatsSet = myChats.stream() 
            .map(m -> m.getChat())
            .collect(Collectors.toSet());

        chats privatechat = null;

        for (members m : targetchats) {
            chats c = m.getChat();
            if (!c.isIsgroup() && myChatsSet.contains(c)) { //common chat for which isgroup = false
                privatechat = c;
                break;
            }
        }
        List<messages> chatMessages = messageRepo.findByChatOrderBySentTimeAsc(privatechat);   
        chatMessages.forEach(msg -> {
                messageDTO dto = new messageDTO(
                    msg.getSender().getName(),
                    msg.getMessage(),
                    msg.getSentTime()
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
    public List<messageDTO> SendMessage(String chatname, String message) { 
        users currentUser = CommonMethods.getCurrentUser();
        if(chatname.equals(currentUser.getName())) throw new nochatFound(chatname);

        String messageId;
        do { messageId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (messageRepo.existsById(messageId));

        if(chatRepo.existsByName(chatname)){
            messages newmsg = new messages(messageId, message, currentUser, chatRepo.findByName(chatname));  
            messageRepo.saveAndFlush(newmsg);
        }
        else if(urepo.existsByName(chatname)){
            users targetUser = urepo.findByName(chatname);

            List<members> myChats = memberRepo.findByMember(currentUser); //chats with current user
            List<members> targetchats = memberRepo.findByMember(targetUser); //chats with target user
            Set<chats> myChatsSet = myChats.stream() .map(m -> m.getChat()).collect(Collectors.toSet());
            chats privatechat = null;

            for (members m : targetchats) {
                chats c = m.getChat();
                if (!c.isIsgroup() && myChatsSet.contains(c)) { //common chat for which isgroup = false
                    privatechat = c;
                    break;
                }
            }

            messages newmsg = new messages(messageId, message, currentUser, privatechat);
            messageRepo.saveAndFlush(newmsg);
        }
        else throw new nochatFound(chatname);

        return Getmessages(chatname);
        
    }

}
