package com.example.messagepublisher;

import org.springframework.http.HttpStatus;

public class Constants {
    public static final String SCHEMA_PATH = "messageSchema.json";



    public static final String QUEUE = "unity_message_queue";
    public static final String EXCHANGE = "unity_message_exchange";
    public static final String ROUTING_KEY = "message_rountingKey";


    public static final Integer ERROR_CODE = HttpStatus.BAD_REQUEST.value();

    //Schema error code
    public static final Integer SCHEMA_ERROR_CODE = HttpStatus.BAD_REQUEST.value();
    public static final String SCHEMA_ERROR_MESSAGE = "Schema is invalid. Please fix your Json!";
}
