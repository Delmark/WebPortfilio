package com.delmark.portfoilo.controller.requests;

import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class ChatCreationRequest {
    String chatName;
    Set<Long> userIds;
}
