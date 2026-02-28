package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        FlashMap flashMap = new FlashMap();
        flashMap.put("errorMessage", "Accès non autorisé. Vous avez été redirigé vers votre tableau de bord.");
        FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
        if (flashMapManager != null) {
            flashMapManager.saveOutputFlashMap(flashMap, request, response);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

            if (roles.contains("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (roles.contains("ROLE_CHEF_CUISINIER")) {
                response.sendRedirect("/orders/chef-dashboard");
                return;
            } else if (roles.contains("ROLE_SERVEUR")) {
                response.sendRedirect("/orders");
                return;
            } else if (roles.contains("ROLE_CLIENT")) {
                response.sendRedirect("/client/dashboard");
                return;
            }
        }

        response.sendRedirect("/");
    }
}
