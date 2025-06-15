package org.example.quanlytaikhoan.service;

import org.example.quanlytaikhoan.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AccountService {
    Account findByEmail(String email);

    List<Account> findAllAccount();

    Account save(Account account);

    Account blockAccount(Long id);

    Account openAccount(Long id);

    Account changeRole(String email, Long roleId);

    Page<Account> searchAccounts(String keyword, Pageable pageable);


}
