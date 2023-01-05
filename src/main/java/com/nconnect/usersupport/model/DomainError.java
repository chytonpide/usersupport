package com.nconnect.usersupport.model;

import java.util.ArrayList;
import java.util.List;

public class DomainError extends Exception {
    private List<String> messages = new ArrayList<>();

    public DomainError() {
        super();
    }

    public DomainError(String message) {
        super();
        messages.add(message);
    }

    public void addMessage(String error) {
        this.messages.add(error);
    }

    public void addMessages(List<String> errors) { this.messages.addAll(errors);}

    public boolean isEmpty() {
        return messages.size() == 0 ?  true : false;
    }

    @Override
    public String getMessage() {
        return formattedMessages();
    }

    public List<String> messages() {
        return messages;
    }

    public String formattedMessages() {
        String result = "";
        for(String error : messages) {
            result = result + error + "\n";
        }

        if(result.length() > 2) {
            result = result.substring(0, result.length()-2);
        }

        return result;
    }
}
