package com.delmark.portfoilo.utils;

import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashSet;
import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User principal =
                new User(1L, "Delmark", "123", "Delmark", "Delmarkovich", null, "gmail@gmail.com", null, true, new HashSet<>(List.of(new Role(1L, "USER"))));

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, "123", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
