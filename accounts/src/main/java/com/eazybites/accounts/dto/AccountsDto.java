package com.eazybites.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
public class AccountsDto {

    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @NotEmpty(message = "Type can not be null or empty")
    private String accountType;

    @NotEmpty(message = "Address can not be null or empty")
    private String branchAddress;
}
