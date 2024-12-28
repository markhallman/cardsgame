package com.markndevon.cardgames.service;

import com.markndevon.cardgames.model.CardGameUser;
import com.markndevon.cardgames.model.UserPrincipal;
import com.markndevon.cardgames.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * This class is used to load the user from the database
 * Not to be confused with the CardsUserService which is used to REGISTER users
 */
@Service
public class CardsUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CardGameUser user = userRepo.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return new UserPrincipal(user);
    }
}
