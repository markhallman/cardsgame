package com.markndevon.cardgames.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Message {

    public abstract MessageType getMessageType();

    @JsonIgnore
    public String getMessageTypeValue() {
        return getMessageType().name();
    }

    public void debugSerialization() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(this);
            System.out.println("Serialized JSON for message type " + getMessageTypeValue() + ": " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
