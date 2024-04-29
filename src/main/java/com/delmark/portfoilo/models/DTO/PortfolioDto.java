package com.delmark.portfoilo.models.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.delmark.portfoilo.models.Portfolio}
 */
@Value
public class PortfolioDto implements Serializable {
    @NotNull
    String name;
    @NotNull
    String surname;
    @NotNull
    String middleName;
    String aboutUser;
    String education;
    @Email
    String email;
    String phone;
    String siteUrl;
}