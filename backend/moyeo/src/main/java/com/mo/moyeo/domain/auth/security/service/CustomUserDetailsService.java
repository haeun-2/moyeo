package com.mo.moyeo.domain.auth.security.service;

import com.mo.moyeo.domain.auth.security.dto.CustomUserDetails;
import com.mo.moyeo.domain.user.entity.User;
import com.mo.moyeo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public CustomUserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = userService.getById(userId);

        return CustomUserDetails.builder()
                .userId(user.getId())
                .role(user.getRole().name())
                .user(user)
                .build();
    }
}
