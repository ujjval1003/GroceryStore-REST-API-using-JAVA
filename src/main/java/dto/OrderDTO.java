/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author ACER
 */
public class OrderDTO {
    private Long orderId;
    private UserDTO userId;
    private BigDecimal totalPrice;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Collection<OrderItemDTO> orderItemsCollection;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public UserDTO getUserId() { return userId; }
    public void setUserId(UserDTO userId) { this.userId = userId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Collection<OrderItemDTO> getOrderItemsCollection() { return orderItemsCollection; }
    public void setOrderItemsCollection(Collection<OrderItemDTO> orderItemsCollection) { this.orderItemsCollection = orderItemsCollection; }
}
