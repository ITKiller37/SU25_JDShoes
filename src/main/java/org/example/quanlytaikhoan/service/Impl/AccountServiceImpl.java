package org.example.quanlytaikhoan.service.Impl;


import org.example.quanlytaikhoan.entity.Account;
import org.example.quanlytaikhoan.entity.Role;
import org.example.quanlytaikhoan.repository.AccountRepository;
import org.example.quanlytaikhoan.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public List<Account> findAllAccount() {
        return accountRepository.findAll();
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }


    @Override
    public Account blockAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(null);
        account.setNonLocked(false);
        return accountRepository.save(account);
    }

    @Override
    public Account openAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(null);
        account.setNonLocked(true);
        return accountRepository.save(account);
    }

    @Override
    public Account changeRole(String email, Long roleId) {
        Account account = accountRepository.findByEmail(email);
        Role role = new Role();
        role.setId(roleId);
        account.setRole(role);
        return accountRepository.save(account);
    }

    @Override
    public Page<Account> searchAccounts(String keyword, Pageable pageable) {
        return accountRepository.searchAccounts(keyword, pageable);
    }


}
