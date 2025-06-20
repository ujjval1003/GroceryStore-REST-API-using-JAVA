/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author ACER
 */
public class CartDTO {
    private Long cartId;
    private UserDTO userId;
    private Date createdAt;
    private Collection<CartItemDTO> cartItemsCollection;

    public Long getCartId() { return cartId; }
    public void setCartId(Long cartId) { this.cartId = cartId; }
    public UserDTO getUserId() { return userId; }
    public void setUserId(UserDTO userId) { this.userId = userId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Collection<CartItemDTO> getCartItemsCollection() { return cartItemsCollection; }
    public void setCartItemsCollection(Collection<CartItemDTO> cartItemsCollection) { this.cartItemsCollection = cartItemsCollection; }
}