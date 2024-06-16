package com.example.ecommerce.config;

import com.example.ecommerce.service.CustomUserService;
import com.example.ecommerce.service.authservice.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private CustomUserService customUserService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String authToken = accessor.getFirstNativeHeader("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
            String username = jwtUtils.extractUsername(authToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserService.loadUserByUsername(username);

                if (jwtUtils.isTokenValid(authToken, userDetails)) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        return message;
    }
}
