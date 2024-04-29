package com.delmark.portfoilo.models.DTO;

import com.delmark.portfoilo.models.User;
import lombok.Value;

import java.io.Serializable;

@Value
public class JwtTokenDTO implements Serializable {
    String token;
}
