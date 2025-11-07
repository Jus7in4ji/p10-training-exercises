package com.justinaji.jproj.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.justinaji.jproj.dto.addmember;
import com.justinaji.jproj.dto.chatdetails;
import com.justinaji.jproj.dto.chatmember;
import com.justinaji.jproj.exception.Username_taken;
import com.justinaji.jproj.exception.formatmismatch;
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
    public chatdetails CreateChat(addmember newGroup) {
        Set<String> membernames = new HashSet<>();
        List<chatmember> groupmembers = newGroup.getmembers();

        if (newGroup.getName() == null || newGroup.getName().isEmpty()|| groupmembers.size()<=1) throw new formatmismatch();
        if (chatRepo.existsByName(newGroup.getName())|| urepo.existsByName(newGroup.getName())) throw new Username_taken();

        String chatId;
        do { chatId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (chatRepo.existsById(chatId));

        users creator = CommonMethods.getCurrentUser();//get logged in user

        chats chat = new chats(chatId, newGroup.getName(),creator,true,""); //new group chat 
        chatRepo.save(chat);
        
        groupmembers.add(new chatmember(creator.getName(), true)); //add current user to memberslist
        groupmembers
        .forEach(newmember -> {

            membernames.add(newmember.getname());
            users memberUser = urepo.findByName(newmember.getname());
            
            members m = new members(chat,memberUser,newmember.getisadmin());
            memberRepo.save(m);
        });
        
        return new chatdetails(newGroup.getName(), creator.getName(), membernames.size(), membernames); 
    }

    @Override
    public String getChats() {
        String uid = CommonMethods.getCurrentUser().getU_id();  //to fetch userid 
        users u = urepo.findById(uid).orElseThrow(() -> new RuntimeException("User id not found: "));
        
        List<members> membershipList = memberRepo.findByMember(u); //find chats the user is part of 
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

    // Admin level commands 
    @Override
    public String AddMember(String chat, String name, boolean isadmin) {
        
        List<members> chatMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        String message = Userauthority(chat, chatMembers);
        if (!message.equals("ok")) return message;

        members targetMember = chatMembers
            .stream().filter(m -> m.getMember().getName().equals(name))
            .findFirst().orElse(null);
        if (targetMember != null) return "Given User is already a member";

        memberRepo.save(new members(chatRepo.findByName(chat),urepo.findByName(name),isadmin));
        return name+ " has been added into "+ chat ;
    }

    @Override
    public String RemoveMember(String chat, String name) {
        List<members> chatMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        String message = Userauthority(chat, chatMembers);
        if (!message.equals("ok")) return message;

        members targetMember = chatMembers // Find if target user inside this chat
            .stream().filter(m -> m.getMember().getName().equals(name))
            .findFirst().orElse(null);
        if (targetMember == null) return "The specified user is not a member of this chat.";
        if(targetMember.isAdmin()) return "Unable to remove: The given user is also an admin ";

        memberRepo.delete(targetMember);


        return name+ " has been removed from "+ chat ;
    }

    @Override
    public String Makeadmin(String chat, String name) {
        List<members> chatMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        String message = Userauthority(chat, chatMembers);
        if (!message.equals("ok")) return message;

        members targetMember = chatMembers // Find if target user inside this chat
            .stream().filter(m -> m.getMember().getName().equals(name))
            .findFirst().orElse(null);
        if (targetMember == null) return "The specified user is not a member of this chat.";


        targetMember.setAdmin(true); // Promote user to admin
        memberRepo.save(targetMember);

        return name + " is now an admin of " + chat;
    }

    @Override
    public String LeaveGroup(String chat){
        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";
        String uid = CommonMethods.getCurrentUser().getU_id();
        List<members> chatMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        
        members loggedInMember = chatMembers //Check if current_user is part of this chat
                .stream().filter(m -> m.getMember().getU_id().equals(uid))
                .findFirst().orElse(null);
                
        if (loggedInMember == null) return "You are not a member of this chat.";

        memberRepo.delete(loggedInMember);
        return "You are no longer a member of "+chat;
    }


    
    String Userauthority(String chat, List<members> chatMembers){

        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";

        String uid = CommonMethods.getCurrentUser().getU_id();
        
        members loggedInMember = chatMembers //Check if current user is part of this chat
                .stream()
                .filter(m -> m.getMember().getU_id().equals(uid))
                .findFirst()
                .orElse(null);
                
        if (loggedInMember == null) return "You are not a member of this chat.";
        if (!loggedInMember.isAdmin()) return "You do not have Admin privileges in this chat.";
        return "ok";
    }

    @Override
    public String getMembers(String chat) {
        
        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";
        List<members> listofMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        if(listofMembers.size()<1) return "No members";

        boolean present = listofMembers.stream().anyMatch(m -> m.getMember().equals(CommonMethods.getCurrentUser()));
        if (!present) return "You are not a member of this chat ";

        StringBuilder sb = new StringBuilder();
        sb.append("Chat ").append(chat);
        listofMembers.forEach(member->{
            sb.append("\n- ").append(member.getMember().getName());
            if(member.isAdmin()) sb.append(" (Admin)");
        });

        return sb.toString();
    }
}
