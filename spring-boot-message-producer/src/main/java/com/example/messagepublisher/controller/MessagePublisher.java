package com.example.messagepublisher.controller;

import com.example.messagepublisher.Util.Util;
import com.example.messagepublisher.dto.MessageInfo;

import com.example.messagepublisher.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    @Autowired
    private MessageService messageService;

    @PostMapping(value = "/publish",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> publishMessage(@RequestBody MessageInfo messageInfo) throws JsonProcessingException {

        // Validate message request
        Map<String, Integer> errorMap = Util.validateRequest(messageInfo);
        if (!errorMap.isEmpty()) {
            StringBuilder errorMessages = new StringBuilder();
            for (Map.Entry<String, Integer> error: errorMap.entrySet()) {
                errorMessages.append(error.getKey() + "\n");
            }
            logger.debug("Message format errors: " + errorMessages);
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }

        // Publish message to queue
        messageService.sendMessageToQueue(messageInfo);

        return new ResponseEntity<>(messageInfo, HttpStatus.CREATED);
    }


}