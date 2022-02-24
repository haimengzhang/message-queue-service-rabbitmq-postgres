package com.example.messageconsumer.controller;

import com.example.messageconsumer.repository.MessageRepository;
import com.example.messageconsumer.service.MessageService;
import com.example.messageconsumer.Util.Constants;
import com.example.messageconsumer.dto.MessageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/message")
public class MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageService messageService;

    @RabbitListener(queues = Constants.QUEUE)
    public void listener(MessageInfo messageInfo) {

        try {
//            messageRepository.save(messageInfo);
            logger.debug("Saving message to db");
            messageService.saveMessageToDB(messageInfo);
        } catch (Exception e) {
            logger.debug("Failed to save message to db.");
        }
    }

    @DeleteMapping
    public String deleteMessagesFromDB() {
        messageRepository.deleteAll();
        return "All messages are deleted";
    }

    @GetMapping
    public ResponseEntity getAllProducts() {
        return ResponseEntity.ok(this.messageRepository.findAll());
    }
}
