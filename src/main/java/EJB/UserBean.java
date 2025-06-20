/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.TokenBlacklist;
import Entity.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class UserBean {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;
    
    private static final String SECRET_KEY = "qwertyuioplkjhgfdsazxcvbnm1234567890";
    private static final long EXPIRY_MS = 86400000;

    // Register a new user or seller
    public Users register(String name, String email, String password, String phone, String address, String role) {
        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role.toUpperCase());
        Date now = new Date();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        em.persist(user);
        return user;
    }

    // Login
    public String login(String email, String password) {
        try {
            Users user = em.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid credentials");
            }
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
            return Jwts.builder()
                    .setSubject(String.valueOf(user.getUserId()))
                    .claim("role", user.getRole())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                    .signWith(SignatureAlgorithm.HS256, key)
                    .compact();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

    // Update user profile
    public Users updateProfile(Long userId, String name, String phone, String address, String password) {
        Users user = em.find(Users.class, userId);
        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            user.setAddress(address);
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setUpdatedAt(new Date());
            em.merge(user);
        }
        return user;
    }

    // Delete user profile
    public void deleteProfile(Long userId) {
        Users user = em.find(Users.class, userId);
        if (user != null) {
            em.remove(user);
        }
    }
    
    // Get user
    public Users getUserById(Long userId) {
        Users user = em.find(Users.class, userId);
        return user;
    }
    
    public void blacklistToken(String token) {
        TokenBlacklist tokentbl = new TokenBlacklist();
        tokentbl.setToken(token);
        tokentbl.setBlacklistedAt(new Date());
        em.persist(tokentbl);
    }

    public boolean isTokenBlacklisted(String token) {
        Long count = em.createQuery("SELECT COUNT(t) FROM TokenBlacklist t WHERE t.token = :token", Long.class)
                .setParameter("token", token)
                .getSingleResult();
        return count > 0;
    }
}