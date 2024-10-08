package com.eazybites.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDto {

    @NotEmpty(message = "Name can not be null or empty")
    @Size(min = 5, max = 60, message = "The length should be between 5 and 60")
    private String name;

    @Email(message = "Email address should be a valid value")
    @NotEmpty(message = "Email can not be null or empty")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    private AccountsDto accountsDto;
}
