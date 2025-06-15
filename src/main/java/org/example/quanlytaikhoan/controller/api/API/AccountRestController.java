package org.example.quanlytaikhoan.controller.api.API;

import org.example.quanlytaikhoan.entity.Account;
import org.example.quanlytaikhoan.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Page<Account>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(accountService.searchAccounts(keyword, pageable));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<String> blockAccount(@PathVariable Long id) {
        Account account = accountService.blockAccount(id);
        return ResponseEntity.ok(" Tài khoản " + account.getEmail() + " đã bị khóa.");
    }

    @PutMapping("/unblock/{id}")
    public ResponseEntity<String> unblockAccount(@PathVariable Long id) {
        Account account = accountService.openAccount(id);
        return ResponseEntity.ok(" Tài khoản " + account.getEmail() + " đã được mở khóa.");
    }

    @PutMapping("/change-role")
    public ResponseEntity<String> changeRole(@RequestParam String email,
                                             @RequestParam Long roleId) {
        Account account = accountService.changeRole(email, roleId);
        return ResponseEntity.ok(" Tài khoản " + account.getEmail() + " đã đổi quyền thành công.");
    }
}
