package com.delmark.portfoilo.models.DTO;

import lombok.Value;

import java.util.Set;

@Value
public class ChatCreationDTO {
    String chatName;
    Set<Long> userIds;
}
