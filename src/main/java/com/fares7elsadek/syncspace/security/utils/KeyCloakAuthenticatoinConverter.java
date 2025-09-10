package com.fares7elsadek.syncspace.security.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

public class KeyCloakAuthenticatoinConverter implements Converter<Jwt, AbstractAuthenticationToken>{
    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";


    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        var authorites = extractAuthorities(source);
        return new JwtAuthenticationToken(source,authorites,getPrincipalClaimName(source));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = jwt.getClaimAsString("preferred_username");
        return claimName != null ? claimName : jwt.getSubject();
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Extract realm roles
        authorities.addAll(extractRealmRoles(jwt));

        // Extract resource/client roles
        authorities.addAll(extractResourceRoles(jwt));

        return authorities;
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt){
        Map<String,Object> realmAcess = jwt.getClaimAsMap(REALM_ACCESS);
        if(realmAcess==null && realmAcess.isEmpty()){
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAcess.get(ROLES);
        return roles.stream().map(role -> ROLE_PREFIX + role.toUpperCase())
                .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt){
        Map<String,Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if(resourceAccess==null && resourceAccess.isEmpty()){
            return new ArrayList<>();
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        resourceAccess.forEach((clientId,clientAccess) ->{
            if(clientAccess instanceof Map){
                @SuppressWarnings("unchecked")
                Map<String,Object> clientMap = (Map<String, Object>) clientAccess;
                if(clientMap.containsKey(ROLES)){
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) clientMap.get(ROLES);
                    authorities.addAll(roles.stream()
                            .map(role -> ROLE_PREFIX + role.toUpperCase())
                            .map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
                }
            }
        });

        return authorities;
    }
}
