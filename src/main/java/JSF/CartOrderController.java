/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JSF;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import api.CartResource.UpdateCartItemRequest;
import dto.CartDTO;
import dto.CartItemDTO;
import dto.OrderDTO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author ACER
 */
@Named
@ViewScoped
public class CartOrderController implements Serializable {
    private static final String CART_API_BASE_URL = "http://localhost:8080/GroceryStore/api/cart";
    private static final String ORDER_API_BASE_URL = "http://localhost:8080/GroceryStore/api/orders";

    @Inject
    private UserController userController;

    private Collection<CartItemDTO> cartItems;
    private BigDecimal totalPrice;
    private List<OrderDTO> orders;
    private Long cartId;
    private String paymentMethod; // For shopcheckout.jsf
    private String cardNumber; // For UI validation
    private String cardName;
    private String expiryDate;
    private String cvv;

    @PostConstruct
    public void init() {
        loadCart();
        loadOrders();
    }

    private Client getClient() {
        return ClientBuilder.newClient();
    }

    public void loadCart() {
        System.out.println("loadCart called for user: " + userController.getUserId());
        if (!userController.isLoggedIn()) {
            cartItems = new ArrayList<>();
            totalPrice = BigDecimal.ZERO;
            cartId = null;
            return;
        }

        Client client = getClient();
        try {
            Response cartResponse = client.target(CART_API_BASE_URL)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userController.getToken())
                    .get();

            if (cartResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                CartDTO cart = cartResponse.readEntity(CartDTO.class);
                cartId = cart.getCartId();
                cartItems = cart.getCartItemsCollection();
                totalPrice = cartItems.stream()
                        .map(item -> item.getProductId().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading cart: " + cartResponse.readEntity(String.class), null));
                cartItems = new ArrayList<>();
                totalPrice = BigDecimal.ZERO;
                cartId = null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading cart: " + e.getMessage(), null));
            cartItems = new ArrayList<>();
            totalPrice = BigDecimal.ZERO;
            cartId = null;
        } finally {
            client.close();
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        if (!userController.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please log in to update cart", null));
            return;
        }

        if (quantity < 1) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Quantity must be at least 1", null));
            return;
        }

        Client client = getClient();
        try {
            Long userId = userController.getUserId();
            int currentQuantity = getCurrentQuantity(productId);
            String endpoint = quantity > currentQuantity ? "/increase" : "/decrease";
            int callsNeeded = Math.abs(quantity - currentQuantity);

            for (int i = 0; i < callsNeeded; i++) {
                // Fix: Split declaration and assignment
                UpdateCartItemRequest request = new UpdateCartItemRequest();
                request.userId = userId;
                request.productId = productId;

                Response response = client.target(CART_API_BASE_URL + endpoint)
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userController.getToken())
                        .put(Entity.json(request));

                if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                    FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating quantity: " + response.readEntity(String.class), null));
                    return;
                }
            }
            loadCart();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Quantity updated successfully!", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating quantity: " + e.getMessage(), null));
        } finally {
            client.close();
        }
    }

    private int getCurrentQuantity(Long productId) {
        return cartItems.stream()
                .filter(item -> item.getProductId().getProductId().equals(productId))
                .map(CartItemDTO::getQuantity)
                .findFirst()
                .orElse(0);
    }

    public void removeItem(Long cartItemId) {
        if (!userController.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please log in to remove item", null));
            return;
        }

        Client client = getClient();
        try {
            Response response = client.target(CART_API_BASE_URL + "/item/" + cartItemId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userController.getToken())
                    .delete();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                loadCart();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item removed from cart!", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error removing item: " + response.readEntity(String.class), null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error removing item: " + e.getMessage(), null));
        } finally {
            client.close();
        }
    }

    public String placeOrder() {
        if (!userController.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please log in to place order", null));
            return "login.jsf?faces-redirect=true";
        }

        if (cartItems == null || cartItems.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cart is empty", null));
            return null;
        }
        
        Client client = getClient();
        try {
            Response response = client.target(CART_API_BASE_URL + "/checkout")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userController.getToken())
                    .post(Entity.json(null));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order placed successfully!", null));
                loadCart();
                loadOrders();
                return "orders.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order placement failed: " + response.readEntity(String.class), null));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order placement error: " + e.getMessage(), null));
            return null;
        } finally {
            client.close();
        }
    }

    public void loadOrders() {
        if (!userController.isLoggedIn()) {
            orders = new ArrayList<>();
            return;
        }

        Client client = getClient();
        try {
            Response response = client.target(ORDER_API_BASE_URL + "/user")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userController.getToken())
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                orders = response.readEntity(new GenericType<List<OrderDTO>>() {});
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading orders: " + response.readEntity(String.class), null));
                orders = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading orders: " + e.getMessage(), null));
            orders = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    public String cancelOrder(Long orderId) {
        System.out.println("Order is canceling");
        if (!userController.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please log in to cancel order", null));
            return "login.jsf?faces-redirect=true";
        }

        Client client = getClient();
        try {
            System.out.println("API calling");
            Response response = client.target(ORDER_API_BASE_URL + "/" + orderId + "/cancel")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + userController.getToken())
                    .put(Entity.json(null));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order cancelled successfully!", null));
                System.out.println("Order canceled");
                loadOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error cancelling order: " + response.readEntity(String.class), null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error cancelling order: " + e.getMessage(), null));
        } finally {
            client.close();
        }
        return null;
    }

    public void clearCartField() {
        cartItems = new ArrayList<>();
        totalPrice = BigDecimal.ZERO;
        cartId = null;
    }
    
    // Getters and Setters
    public Collection<CartItemDTO> getCartItems() { return cartItems; }
    public void setCartItems(Collection<CartItemDTO> cartItems) { this.cartItems = cartItems; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public List<OrderDTO> getOrders() { return orders; }
    public void setOrders(List<OrderDTO> orders) { this.orders = orders; }
    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}