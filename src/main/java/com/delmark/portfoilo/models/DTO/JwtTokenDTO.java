package com.delmark.portfoilo.models.DTO;

import lombok.Value;

import java.io.Serializable;

@Value
public class JwtTokenDTO implements Serializable {
    String token;
}
