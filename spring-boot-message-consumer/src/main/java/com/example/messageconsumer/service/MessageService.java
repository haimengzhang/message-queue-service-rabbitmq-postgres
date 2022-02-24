package com.example.messageconsumer.service;

import com.example.messageconsumer.repository.MessageRepository;
import com.example.messageconsumer.Util.Constants;
import com.example.messageconsumer.dto.MessageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /** Save message to database
     *
     * @param messageInfo
     * @return
     * @throws JsonProcessingException
     */
    public String saveMessageToDB(MessageInfo messageInfo) {

        messageRepository.save(messageInfo);

        return "Saving to db";
    }

}
