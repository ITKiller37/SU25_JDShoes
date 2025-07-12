package com.example.jdshoes.service;



import com.example.jdshoes.dto.Account.AccountDto;
import com.example.jdshoes.dto.Account.ChangePasswordDto;
import com.example.jdshoes.dto.Statistic.UserStatistic;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.enumClass.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Account findByEmail(String email);

    List<Account> findAllAccount();

    Page<Account> findByRoleWithPaging(RoleName role, Pageable pageable);

    Page<Account> searchEmployeeByEmailOrName(RoleName role, String keyword, Pageable pageable);

    Account save(Account account);

    List<UserStatistic> getUserStatistics(String startDate, String endDate);

    Account blockAccount(Long id);

    Account openAccount(Long id);

    Account changeRole(String email, Long roleId);

    AccountDto getAccountLogin();

    AccountDto updateProfile(AccountDto accountDto);

    void changePassword(ChangePasswordDto changePasswordDto);

    void resetPassword(Account account, String newPassword);
}
