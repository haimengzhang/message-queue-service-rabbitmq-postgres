package com.example.messagepublisher.service;

import com.example.messagepublisher.Constants;
import com.example.messagepublisher.dto.MessageInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private RabbitTemplate template;

    /** Publish request message to queue
     *
     * @param messageInfo
     * @throws JsonProcessingException
     */
    public void sendMessageToQueue(MessageInfo messageInfo) {

        try {
            messageInfo.setMessageAttributes(messageInfo.getMessageAttributes());
            messageInfo.serializeMessageAttributes();

            // convert and send the message
            template.convertAndSend(Constants.EXCHANGE, Constants.ROUTING_KEY, messageInfo);
        } catch (JsonProcessingException | AmqpException e) {
            logger.debug("Error publishing the message to queue, " + e.getMessage());
        }
    }

}
