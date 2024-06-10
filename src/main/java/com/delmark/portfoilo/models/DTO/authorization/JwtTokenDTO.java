package com.delmark.portfoilo.models.DTO.authorization;

import lombok.Value;

import java.io.Serializable;

@Value
public class JwtTokenDTO implements Serializable {
    String token;
}
