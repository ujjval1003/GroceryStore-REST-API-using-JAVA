/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JSF;

import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author ACER
 */
@Named
@SessionScoped
public class UserController implements Serializable {
    private static final String API_BASE_URL = "http://localhost:8080/GroceryStore/api/users";
    private String token; // JWT
    private Long userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String role = "USER";

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    private Client getClient() {
        return ClientBuilder.newClient();
    }
    
    public String register() {
        Client client = getClient();
        try {
            Response response = client.target(API_BASE_URL + "/register")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(new RegisterRequest(name, email, password, phone, address, role)));
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration successful! Please login."));
                clearFields();
                return "login.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String login() {
        Client client = getClient();
        try {
            Response response = client.target(API_BASE_URL + "/login")
                    .request(MediaType.TEXT_PLAIN)
                    .post(Entity.json(new LoginRequest(email, password)));
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                token = response.readEntity(String.class);
                // Extract userId from token and fetch profile
                String[] tokenParts = token.split("\\.");
                String payload = new String(java.util.Base64.getDecoder().decode(tokenParts[1]));
                userId = Long.parseLong(payload.split("\"sub\":\"")[1].split("\"")[0]);
                role = payload.split("\"role\":\"")[1].split("\"")[0];
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
                session.setAttribute("userId", userId);
                session.setAttribute("role", role);
                loadUserProfile();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login successful!"));
                if (role.equals("SELLER")) {
                    SellerController sellerController = FacesContext.getCurrentInstance().getApplication()
                            .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{sellerController}", SellerController.class);
                    sellerController.initSellerId(userId, token, role);
                    return "sellerproducts.jsf?faces-redirect=true";
                } else if (role.equals("USER")){
                    return "shop.jsf?faces-redirect=true";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials", null));
                    return null;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid credentials", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String updateProfile() {
        Client client = getClient();
        loadUserProfile();
        try {
            Response response = client.target(API_BASE_URL + "/profile")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .put(Entity.json(new UpdateProfileRequest(userId, name, phone, address, password)));
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profile updated successfully!"));
                loadUserProfile();
                return "accountsetting.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String deleteProfile() {
        Client client = getClient();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        try {
            Response response = client.target(API_BASE_URL + "/profile/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .delete();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profile deleted successfully!"));
                if (session != null) {
                    session.removeAttribute("userId");
                    session.removeAttribute("role");
                    session.invalidate();
                }
                token = null;
                userId = null;
                clearFields();
                return "login.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Deletion failed", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String logout() {
        Client client = getClient();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
        try {
            Response response = client.target(API_BASE_URL + "/logout")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .post(Entity.json(null));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Logged out successfully!"));
        } catch (Exception e) {
            System.out.println("Logout error: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Logout error: " + e.getMessage(), null));
        } finally {
            if (session != null) {
                session.removeAttribute("userId");
                session.removeAttribute("role");
                session.invalidate();
            }
            token = null;
            userId = null;
            clearFields();
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            client.close();
        }
        return "login.jsf?faces-redirect=true";
    }

    private void loadUserProfile() {
        Client client = getClient();
        try {
            Response response = client.target(API_BASE_URL + "/profile/" + userId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                UserResponse user = response.readEntity(UserResponse.class);
                name = user.name;
                email = user.email;
                phone = user.phone;
                address = user.address;
                password = user.password;
                role = user.role;
            }
        } finally {
            client.close();
        }
    }

    private void clearFields() {
        name = null;
        email = null;
        password = null;
        phone = null;
        address = null;
        role = "USER";
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    // DTO classes for JSON serialization
    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public String phone;
        public String address;
        public String role;

        public RegisterRequest(String name, String email, String password, String phone, String address, String role) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.address = address;
            this.role = role;
        }
    }

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class UpdateProfileRequest {
        public Long userId;
        public String name;
        public String phone;
        public String address;
        public String password;

        public UpdateProfileRequest(Long userId, String name, String phone, String address, String password) {
            this.userId = userId;
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.password = password;
        }
    }

    public static class UserResponse {
        public Long userId;
        public String name;
        public String email;
        public String phone;
        public String address;
        public String role;
        private String password;
    }
}