package com.justinaji.chatapp_messages.RestClientAPIs;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.justinaji.chatapp_messages.dto.TempMsg;

@HttpExchange(url = "${tempmsg.service.url}", accept= "application/json")
public interface TempMsgs {
    
    @GetExchange("/temp/returnchats")
    List<TempMsg> getTempMessages(@RequestParam("sender") String sender);
}
