package com.justinaji.chatapp_userchats.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.justinaji.chatapp_userchats.dto.addmember;
import com.justinaji.chatapp_userchats.dto.chatdetails;
import com.justinaji.chatapp_userchats.dto.chatmember;
import com.justinaji.chatapp_userchats.exception.NoUserFound;
import com.justinaji.chatapp_userchats.exception.NotaMember;
import com.justinaji.chatapp_userchats.exception.Username_taken;
import com.justinaji.chatapp_userchats.exception.formatmismatch;
import com.justinaji.chatapp_userchats.model.Logs;
import com.justinaji.chatapp_userchats.model.chats;
import com.justinaji.chatapp_userchats.model.members;
import com.justinaji.chatapp_userchats.model.users;
import com.justinaji.chatapp_userchats.repository.ChatRepo;
import com.justinaji.chatapp_userchats.repository.LogRepo;
import com.justinaji.chatapp_userchats.repository.MemberRepo;
import com.justinaji.chatapp_userchats.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class chat_servicesimpl implements chat_services {

    private final ChatRepo chatRepo;
    private final MemberRepo memberRepo;
    private final UserRepo urepo;
    private final LogRepo logRepo;
    public chat_servicesimpl(ChatRepo chatRepo, MemberRepo memberRepo,UserRepo urepo, LogRepo logRepo){
        this.chatRepo = chatRepo;
        this.memberRepo = memberRepo;
        this.urepo = urepo;
        this.logRepo = logRepo;
    }

    @Override
    @Transactional
    public chatdetails CreateChat(addmember newGroup) {
        Set<String> membernames = new HashSet<>();
        List<chatmember> groupmembers = newGroup.getMembers();

        if (newGroup.getName() == null || newGroup.getName().isEmpty()|| groupmembers.size()<=1) throw new formatmismatch();
        if (chatRepo.existsByName(newGroup.getName())|| urepo.existsByName(newGroup.getName())) throw new Username_taken();

        String chatId;
        do { chatId = CommonMethods.getAlphaNumericString(); } //generate unique chat id
        while (chatRepo.existsById(chatId));

        users creator = CommonMethods.getCurrentUser();//get logged in user

        chats chat = new chats(chatId, newGroup.getName(),creator,true, CommonMethods.generateKey()); //new group chat 
        chatRepo.save(chat);
        
        groupmembers.add(new chatmember(creator.getName(), true)); //add current user to memberslist
        groupmembers
        .forEach(newmember -> {
            String name = newmember.getName();
            
            if(!urepo.existsByName(newmember.getName())) throw new NoUserFound(name);
            membernames.add(name);
            users memberUser = urepo.findByName(name);
            
            members m = new members(chat,memberUser,newmember.isAdmin());
            memberRepo.save(m);
        });
        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logRepo.existsById(logid));

        Logs l = new Logs(logid, "Chat Creation", "New Groupchat '"+newGroup.getName()+"' has been created.", new Timestamp(System.currentTimeMillis()), CommonMethods.getCurrentUser());
        logRepo.save(l);
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

                if (other != null) sb.append("\n- ").append(other.getName()).append("");
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

        if(!urepo.existsByName(name)) throw new  NoUserFound(name);
        memberRepo.save(new members(chatRepo.findByName(chat),urepo.findByName(name),isadmin));

        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logRepo.existsById(logid));

        Logs l = new Logs(logid, "Addition to chat", "User '"+name+"' has been added to the chat '"+chat+"'.", new Timestamp(System.currentTimeMillis()), CommonMethods.getCurrentUser());
        logRepo.save(l);

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
        if (targetMember == null) throw new NotaMember(name, chat); 
        if(targetMember.isAdmin()) return "Unable to remove: The given user is also an admin ";

        memberRepo.delete(targetMember);

        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logRepo.existsById(logid));

        Logs l = new Logs(logid, "Removal from chat", "User '"+name+"' has been removed from the chat '"+chat+"'.", new Timestamp(System.currentTimeMillis()), CommonMethods.getCurrentUser());
        logRepo.save(l);

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
        if (targetMember == null) throw new NotaMember(name, chat); 


        targetMember.setAdmin(true); // Promote user to admin
        memberRepo.save(targetMember);

        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logRepo.existsById(logid));

        Logs l = new Logs(logid, "Granted Admin Role", "User '"+name+"' has granted Admin role in the chat '"+chat+"'.", new Timestamp(System.currentTimeMillis()), CommonMethods.getCurrentUser());
        logRepo.save(l);

        return name + " is now an admin of " + chat;
    }

    @Override
    public String LeaveGroup(String chat){
        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";
        users current_user = CommonMethods.getCurrentUser();
        List<members> chatMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        
        members loggedInMember = chatMembers //Check if current_user is part of this chat
                .stream().filter(m -> m.getMember().equals(current_user))
                .findFirst().orElse(null);
                
        if (loggedInMember == null) throw new NotaMember(current_user.getName(), chat); 

        memberRepo.delete(loggedInMember);

        String logid;
            
        do { logid = CommonMethods.getAlphaNumericString(); } 
        while (logRepo.existsById(logid));

        Logs l = new Logs(logid, "User Exited", "User '"+CommonMethods.getCurrentUser().getName()+"' has left the chat '"+chat+"'.", new Timestamp(System.currentTimeMillis()), CommonMethods.getCurrentUser());
        logRepo.save(l);

        return "You are no longer a member of "+chat;
    }


    
    String Userauthority(String chat, List<members> chatMembers){

        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";

        users current_user = CommonMethods.getCurrentUser();
        
        members loggedInMember = chatMembers //Check if current user is part of this chat
                .stream()
                .filter(m -> m.getMember().equals(current_user))
                .findFirst()
                .orElse(null);
                
        if (loggedInMember == null) throw new NotaMember(current_user.getName(), chat); 
        if (!loggedInMember.isAdmin()) return "You do not have Admin privileges in this chat.";
        return "ok";
    }

    @Override
    public String getMembers(String chat) {
        users current_user = CommonMethods.getCurrentUser();

        if (!chatRepo.existsByName(chat)) return "There exists no group of name '"+chat+"'.";
        List<members> listofMembers = memberRepo.findByChat(chatRepo.findByName(chat));
        if(listofMembers.size()<1) return "No members";

        boolean present = listofMembers.stream().anyMatch(m -> m.getMember().equals(current_user));
        if (!present) throw new NotaMember(current_user.getName(), chat); 

        StringBuilder sb = new StringBuilder();
        sb.append("Chat ").append(chat);
        listofMembers.forEach(member->{
            sb.append("\n- ").append(member.getMember().getName());
            if(member.isAdmin()) sb.append(" (Admin)");
        });

        return sb.toString();
    }

    @Override
    public HashMap<String,String> ischatvalid(String chatname, String username, boolean isgroup){
        HashMap<String , String> result = new HashMap<>();
        result.put("roomid", null);

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
