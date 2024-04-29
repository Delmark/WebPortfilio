package com.delmark.portfoilo.models.DTO;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.delmark.portfoilo.models.User}
 */
@Value
public class UserDto implements Serializable {
    String username;
    String password;
}