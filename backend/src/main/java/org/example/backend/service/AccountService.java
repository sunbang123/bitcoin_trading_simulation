package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.entity.Account;
import org.example.backend.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountByCurrency(Long userId, String currency) {
        return accountRepository.findByUserIdAndCurrency(userId, currency);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
