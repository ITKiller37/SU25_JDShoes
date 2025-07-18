package com.example.jdshoes.sercurity;


import com.example.jdshoes.entity.Account;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository acccountRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = acccountRepository.findByEmail(username);

        if (account != null) {
            return new CustomUserDetails(account);
        }
        throw new UsernameNotFoundException("Không tìm thấy tài khoản có username là: " + username);
    }
}
