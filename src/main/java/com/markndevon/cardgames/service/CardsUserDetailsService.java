package com.markndevon.cardgames.service;

import com.markndevon.cardgames.model.UserPrincipal;
import com.markndevon.cardgames.model.Users;
import com.markndevon.cardgames.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CardsUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepo.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return new UserPrincipal(user);
    }
}
