package org.example.quanlytaikhoan.controller.api.admin;


import org.example.quanlytaikhoan.entity.Account;
import org.example.quanlytaikhoan.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/admin-only")
public class AccountMngController {

    private final AccountService accountService;

    public AccountMngController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/account-management")
    public String viewAccountManagementPage(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "") String keyword,
                                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountService.searchAccounts(keyword, pageable);

        model.addAttribute("accountList", accounts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", accounts.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "/admin/account";
    }

    @PostMapping("/account/block/{id}")
    public String blockAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.blockAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã khóa thành công");
        return "redirect:/admin-only/account-management";
    }

    @PostMapping("/account/open/{id}")
    public String openAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.openAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã mở khóa thành công");
        return "redirect:/admin-only/account-management";
    }

    @PostMapping("/account/change-role")
    public String openAccount(@ModelAttribute("email") String email, @ModelAttribute("role") Long roleId, RedirectAttributes redirectAttributes) {
        Account account = accountService.changeRole(email, roleId);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã đổi thành quyền thành công");
        return "redirect:/admin-only/account-management";
    }

    @GetMapping("/api/accounts")
    @ResponseBody
    public Page<Account> getAccountsJson(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,
                                         @RequestParam(defaultValue = "") String keyword) {
        return accountService.searchAccounts(keyword, PageRequest.of(page, size));
    }
}
