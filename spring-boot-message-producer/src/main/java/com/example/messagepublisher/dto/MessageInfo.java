package com.example.messagepublisher.dto;

import com.example.messagepublisher.converter.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "messages")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(required = true)
    @Column(name = "ts")
    String ts;

    @JsonProperty(required = true)
    @Column(name = "sender")
    String sender;

    @Column(name = "sentFromIp")
    String sent_from_ip;

    @Column(name = "priority")
    Integer priority;

    @Column(name = "messageAttributes")
    private String messageAttributesJson;

    @Transient
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> messageAttributes;

    public MessageInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSent_from_ip() {
        return sent_from_ip;
    }

    public void setSentFromIp(String sentFromIp) {
        this.sent_from_ip = sentFromIp;
    }

    public String getMessageAttributesJson() {
        return messageAttributesJson;
    }

    public void setMessageAttributesJson(String messageAttributesJson) {
        this.messageAttributesJson = messageAttributesJson;
    }

    public Map<String, Object> getMessageAttributes() {
        return messageAttributes;
    }

    public void setMessageAttributes(Map<String, Object> messageAttributes) {
        this.messageAttributes = messageAttributes;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void serializeMessageAttributes() throws JsonProcessingException {
        this.messageAttributesJson = new ObjectMapper().writeValueAsString(messageAttributes);
    }

    public void deserializeMessageAttributes() throws IOException {
        this.messageAttributes = new ObjectMapper().readValue(messageAttributesJson, HashMap.class);
    }

}
