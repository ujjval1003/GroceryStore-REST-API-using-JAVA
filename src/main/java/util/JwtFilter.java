/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import EJB.UserBean;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import javax.crypto.spec.SecretKeySpec;

import java.security.Key;

/**
 *
 * @author ACER
 */
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {
    private static final String SECRET_KEY = "qwertyuioplkjhgfdsazxcvbnm1234567890";

    @Inject
    private UserBean userBean;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Check for @Secured on method or class
        Secured secured = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }

        // If no @Secured annotation, skip filtering
        if (secured == null) {
            return;
        }

        // Extract Authorization header
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid token").build());
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();

        if (userBean.isTokenBlacklisted(token)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token is blacklisted").build());
            return;
        }

        try {
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            if (userId == null || userId.trim().isEmpty()) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token: missing userId").build());
                return;
            }

            if (secured.role().length() > 0 && !secured.role().equalsIgnoreCase(role)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Role not allowed").build());
                return;
            }

            requestContext.setProperty("userId", userId);
            requestContext.setProperty("role", role);
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or expired token: " + e.getMessage()).build());
        }
    }
}