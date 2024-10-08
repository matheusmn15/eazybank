package com.eazybites.accounts.service;

import com.eazybites.accounts.dto.CustomerDto;

public interface AccountsService {

    void createAccount(CustomerDto request);

    CustomerDto fetchAccount(String mobileNumber);

    boolean updateAccount(CustomerDto customerDto);

    boolean deleteAccount(String mobileNumber);
}
