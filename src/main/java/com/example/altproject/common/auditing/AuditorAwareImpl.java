package com.example.altproject.common.auditing;

import com.example.altproject.filter.CustomUserDetails;
import com.example.altproject.service.oauth.CustomOAuth2User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User oauthUser) {
            return Optional.of(oauthUser.getEmail());
        } else if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getUsername());
        }

        return Optional.empty();
    }
}
