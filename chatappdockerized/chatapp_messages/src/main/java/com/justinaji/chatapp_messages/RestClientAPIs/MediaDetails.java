package com.justinaji.chatapp_messages.RestClientAPIs;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.justinaji.chatapp_messages.model.media;

@HttpExchange(url ="${media.service.url}", accept = "application/json")
public interface MediaDetails {

    @GetExchange("/media/getchatmedia")
    List<media> getMedia(@RequestParam("chatid") String chatid);

    @GetExchange("/media/getfile")
    media getFile(@RequestParam("id") String id);
    
    @GetExchange("/media/idlist")
    List<String> ListofIds();
}
