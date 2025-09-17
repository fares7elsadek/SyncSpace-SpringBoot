package com.fares7elsadek.syncspace.security.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null || !context.getAuthentication().isAuthenticated()) {
            return Optional.of("system");
        }
        return Optional.ofNullable(context.getAuthentication().getName());
    }
}

