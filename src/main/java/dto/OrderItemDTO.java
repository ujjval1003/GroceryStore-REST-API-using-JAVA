/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.math.BigDecimal;

/**
 *
 * @author ACER
 */
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal discountApplied;

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    public ProductDTO getProductId() { return productId; }
    public void setProductId(ProductDTO productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    public BigDecimal getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }
}
