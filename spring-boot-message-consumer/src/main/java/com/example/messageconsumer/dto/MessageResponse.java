package com.example.messageconsumer.dto;

import java.util.Map;

public class MessageResponse {

    String id;

    String sender;

    String ts;

    private Map<String, Object> messageAttributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Map<String, Object> getMessageAttributes() {
        return messageAttributes;
    }

    public void setMessageAttributes(Map<String, Object> messageAttributes) {
        this.messageAttributes = messageAttributes;
    }
}
