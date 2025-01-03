package com.pathcreator.hive.security;

import com.pathcreator.hive.exception.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@WebFilter(asyncSupported = true)
public class JwtFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "access_token";
    public static final String EXPECTED_AUTH_SCHEME = "bearer";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath() + ((request.getPathInfo() != null) ? request.getPathInfo() : "");
        return "/".equals(path) || path.startsWith("/swagger-ui") || path.startsWith("/v1/openapi");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String encodedJwtToken = getHeaderJwtToken(request);
        if (encodedJwtToken == null) {
            encodedJwtToken = getCookieJwtToken(request);
        }
        try {
            if ((encodedJwtToken != null) && !encodedJwtToken.isEmpty()) {
                if (jwtTokenUtil.isTokenExpired(encodedJwtToken)) {
                    log.warn("Token is expired");
                    throw new ApiException(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
                }
                Collection<String> perms = jwtTokenUtil.getPermsFromToken(encodedJwtToken);
                Set<SimpleGrantedAuthority> authorities = perms.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(true, jwtTokenUtil.getUserIdFromToken(encodedJwtToken), authorities));
            } else {
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(true, null, new HashSet<>()));
            }
        } catch (RuntimeException ex) {
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(false, null, new HashSet<>()));
        }
        filterChain.doFilter(request, response);
    }

    private static String getCookieJwtToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
        return (cookie != null) ? cookie.getValue() : null;
    }

    private static String getHeaderJwtToken(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        String[] parts = (auth == null) ? null : auth.split(" ");
        if ((parts == null) || !EXPECTED_AUTH_SCHEME.equalsIgnoreCase(parts[0]) || (parts.length != 2)) {
            return null;
        }
        return parts[1];
    }
}