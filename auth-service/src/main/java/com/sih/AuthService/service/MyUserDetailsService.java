package com.sih.AuthService.service;

import com.sih.AuthService.model.UserPrincipal;
import com.sih.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService { // for mapping User to UserPrincipal
    private final UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String loginString) throws UsernameNotFoundException {
        // since this function requires UserDetails so we give it
        return repo.findByUsername(loginString)
                .or(() -> repo.findByEmail(loginString))
                .map(UserPrincipal::new)
                .orElseThrow(() -> new RuntimeException("User not found"));// in java, class has to be functional interface to have its constructor reference
    }
}
