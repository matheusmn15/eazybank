package com.eazybites.accounts.service.impl;

import com.eazybites.accounts.constants.AccountsConstants;
import com.eazybites.accounts.dto.AccountsDto;
import com.eazybites.accounts.dto.CustomerDto;
import com.eazybites.accounts.entity.Accounts;
import com.eazybites.accounts.entity.Customer;
import com.eazybites.accounts.exception.CustomerAlreadyExistsException;
import com.eazybites.accounts.exception.ResourceNotFoundException;
import com.eazybites.accounts.mapper.AccountsMapper;
import com.eazybites.accounts.mapper.CustomerMapper;
import com.eazybites.accounts.repository.AccountsRepository;
import com.eazybites.accounts.repository.CustomerRepository;
import com.eazybites.accounts.service.AccountsService;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;

    @Override
    public void createAccount(CustomerDto request) {
        var customer = CustomerMapper.mapToCustomer(request, new Customer());
        customerRepository.findByMobileNumber(customer.getMobileNumber()).ifPresent(c -> {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists with this mobile number: " + c.getMobileNumber());
        });

        var savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository
                .findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        Accounts accounts = accountsRepository
                .findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account", "customerId", customer.getCustomerId().toString()));
        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts accounts = accountsRepository
                    .findById(accountsDto.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Account",
                            "AccountNumber",
                            accountsDto.getAccountNumber().toString()));
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository
                    .findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString()));
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository
                .findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
