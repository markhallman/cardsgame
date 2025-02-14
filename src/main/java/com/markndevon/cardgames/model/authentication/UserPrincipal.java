package com.markndevon.cardgames.model.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

   private CardGameUser user;

    public UserPrincipal(CardGameUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: Figure out what this actually does
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
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
        // TODO: We have no current way to track account expiration
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO: We have no current way to track locking accounts
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: We have no current way to track credentials expiration
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
