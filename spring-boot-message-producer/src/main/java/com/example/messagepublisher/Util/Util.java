package com.example.messagepublisher.Util;

import com.example.messagepublisher.Constants;
import com.example.messagepublisher.dto.MessageInfo;
import com.example.messagepublisher.service.MessageService;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpStatus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.example.messagepublisher.Constants.*;

public class Util {

    public static Map<String, Integer> validateRequest(MessageInfo messageInfo) {

        if (!validateSchema(messageInfo)) {
            Map<String, Integer> errors = new HashMap<>();
            errors.put(SCHEMA_ERROR_MESSAGE, SCHEMA_ERROR_CODE);
            return errors;
        }
        else {
            return validateMessageInfo(messageInfo);
        }
    }

    /** Validate incoming message json against schema definition
     *
     * @param messageInfo
     * @return
     */
    public static boolean validateSchema(MessageInfo messageInfo) {

        try (InputStream schemaAsStream = new FileInputStream(Constants.SCHEMA_PATH)) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(schemaAsStream));
            Schema schema = SchemaLoader.load(rawSchema);
            Gson gson = new Gson();
            schema.validate(new JSONObject(gson.toJson(messageInfo)));
        } catch (ValidationException | FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /** Validate incoming messageInfo from request body
     *
     * @param messageInfo
     * @return
     */
    public static Map<String, Integer> validateMessageInfo(MessageInfo messageInfo) {

        long now = Instant.now().getEpochSecond();
        Integer timeStamp = Integer.parseInt(messageInfo.getTs());
        String sender = messageInfo.getSender();
        String ipv4 = messageInfo.getSent_from_ip();
        InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

        Map<String, Integer> errorMessages = new HashMap<>();

        if (timeStamp == null) {
            errorMessages.put("Timestamp is required.", ERROR_CODE);
        }

        if ((long) timeStamp >= now) {
            errorMessages.put("Timestamp " + timeStamp + " is not a valid unix timestamp.", ERROR_CODE);
        }

        if (sender == null) {
            errorMessages.put("Sender must not be null", ERROR_CODE);
        }

        if (ipv4 != null && !inetAddressValidator.isValid(ipv4)) {
            errorMessages.put("The IP address " + ipv4 + " isn't valid", ERROR_CODE);
        }

        if (messageInfo.getMessageAttributes() == null || messageInfo.getMessageAttributes().isEmpty()) {
            errorMessages.put("Message attributes cannot be null", ERROR_CODE);
        }
        return errorMessages;
    }

}
