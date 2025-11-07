package com.justinaji.jproj.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.justinaji.jproj.exception.formatmismatch;
import com.justinaji.jproj.model.CurrentUser;
import com.justinaji.jproj.dto.addmember;
import com.justinaji.jproj.dto.chatmember;
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
        users creator = user.getUser();

        groupmembers.add(new chatmember(creator.getName(), true));
        
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

            users memberUser = urepo.findByName(newmember.getname());
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

        if (membershipList.isEmpty()) return "You have no chats yet.";

        StringBuilder sb = new StringBuilder();
        sb.append("Available chats:");

        membershipList.forEach(m -> {
            chats c = m.getChat();

            if (c.isIsgroup()) sb.append("\n- Groupchat: ( ").append(c.getName()).append(" )"); 
            else {
                List<members> twoUsers = memberRepo.findByChat(c);
                users other = twoUsers.stream()
                        .map(memb -> memb.getMember())
                        .filter(member -> !member.getU_id().equals(uid))
                        .findFirst()
                        .orElse(null); 

                if (other != null) sb.append("\n- Chat with: ").append(other.getName()).append("");
                else sb.append("\n- Personal chat (unknown user)");
            }   
        });

        return sb.toString();
    }

}
