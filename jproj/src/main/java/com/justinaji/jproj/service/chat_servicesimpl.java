package com.justinaji.jproj.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.justinaji.jproj.exception.formatmismatch;
import com.justinaji.jproj.model.CurrentUser;
import com.justinaji.jproj.model.addmember;
import com.justinaji.jproj.model.chatmember;
import com.justinaji.jproj.model.chats;
import com.justinaji.jproj.model.members;
import com.justinaji.jproj.model.users;
import com.justinaji.jproj.repository.ChatRepo;
import com.justinaji.jproj.repository.MemberRepo;
import com.justinaji.jproj.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class chat_servicesimpl implements chat_services {

    private final ChatRepo chatRepo;
    private final MemberRepo memberRepo;
    private final UserRepo urepo;
    public chat_servicesimpl(ChatRepo chatRepo, MemberRepo memberRepo,UserRepo urepo){
        this.chatRepo = chatRepo;
        this.memberRepo = memberRepo;
        this.urepo = urepo;
    }

    @Override
    @Transactional
    public chats CreateChat(addmember newGroup) {
        if (newGroup.getName() == null || newGroup.getName().isEmpty()) throw new formatmismatch();
        List<chatmember> groupmembers = newGroup.getmembers();
        if(groupmembers.size()<=1)throw new formatmismatch();


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();
        String uid = user.getUser().getU_id();  //to fetch userid 
        chatmember current_user = new chatmember(uid, true);
        users creator = urepo.findById(uid)
                .orElseThrow(() -> new RuntimeException("User id not found: "));

        groupmembers.add(current_user);
        
        String chatId;
        do { chatId = CommonMethods.getAlphaNumericString(); } 
        while (chatRepo.existsById(chatId));

        chats chat = new chats(); //new 1-1 chat 
        chat.setName(newGroup.getName());
        chat.setC_id(chatId);
        chat.setIsgroup(true);
        chat.setCreatedBy(creator);
        chatRepo.save(chat);

        groupmembers
        .forEach(newmember -> {

            users memberUser = urepo.findById(newmember.getid())
                .orElseThrow(() -> new RuntimeException("User id not found: " + newmember.getid()));

            members m = new members();
            m.setChat(chat);
            m.setMember(memberUser);
            m.setAdmin(newmember.getisadmin());
            memberRepo.save(m);
        });

    return chat; 
    }

    @Override
    public String getChats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser user = (CurrentUser) auth.getPrincipal();
        String uid = user.getUser().getU_id();  //to fetch userid 
        users u = urepo.findById(uid).orElseThrow(() -> new RuntimeException("User id not found: "));
        List<members> membershipList = memberRepo.findByMember(u);

        if (membershipList.isEmpty()) {
            return "You have no chats yet.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Available chats:\n");

        membershipList.forEach(m -> {
            chats c = m.getChat();

            if (c.isIsgroup()) sb.append("- Group: ").append(c.getName()).append(" (ID: ").append(c.getC_id()).append(")\n"); 
            else sb.append("- Personal chat (ID: ").append(c.getC_id()).append(")\n");
            
        });

        return sb.toString();
    }

}
