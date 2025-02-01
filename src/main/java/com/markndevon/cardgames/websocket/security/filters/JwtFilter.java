package com.markndevon.cardgames.websocket.security.filters;

import com.markndevon.cardgames.controller.UserController;
import com.markndevon.cardgames.logger.Logger;
import com.markndevon.cardgames.service.authentication.CardsUserDetailsService;
import com.markndevon.cardgames.service.authentication.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTService jwtService;

    @Autowired
    private CardsUserDetailsService userDetailsService;

    @Autowired
    private Logger logger;

    @Autowired
    @Lazy
    private UserController userController;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.log("Applying JWT Filter");

        UserDetails userDetails = null;
        ResponseEntity<?> authResponse = userController.checkAuth(request);
        if(authResponse.getStatusCode() == HttpStatus.OK){
            userDetails = (UserDetails) authResponse.getBody();
            logger.log("User is OK");

        }

        if(userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
