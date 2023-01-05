package com.nconnect.usersupport.infrastructure;

import com.nconnect.usersupport.model.DomainError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorData {

    public final List<String> messages;

    public static ErrorData from(final String message) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        return new ErrorData(messages);
    }

    public static ErrorData from(final List<String> messages) {
        return new ErrorData(messages);
    }

    public static ErrorData empty() {
        return new ErrorData(Collections.emptyList());
    }

    public ErrorData(final List<String> messages) {
        this.messages = messages;

    }
}
