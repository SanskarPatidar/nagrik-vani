package com.sih.AuthService.model;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/*
QUESTION: Why You Do This Step?
ANSWER:
Spring Security doesn’t know about your User class (with fields like username, password, and roles). So, it expects a UserDetails implementation that provides user credentials and authorities in a specific format.

UserPrincipal acts as a bridge between:
Your custom User entity (from MongoDB)

What Spring Security expects (UserDetails)
*/

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public String getId() {
        return user.getId();
    }
    public String getEmail() {
        return user.getEmail();
    }
    public List<Role> getRoles() { return user.getRoles(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // AUTHORIZATION
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toString()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}