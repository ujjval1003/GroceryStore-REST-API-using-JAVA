/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ACER
 */
public class SellerAnalyticsDTO {
    private Long analyticId;
    private UserDTO sellerId;
    private BigDecimal totalSales;
    private Integer totalOrders;
    private ProductDTO topProductId;
    private Date lastUpdated;

    // Constructors
    public SellerAnalyticsDTO() {
    }

    // Getters and Setters
    public Long getAnalyticId() {
        return analyticId;
    }

    public void setAnalyticId(Long analyticId) {
        this.analyticId = analyticId;
    }

    public UserDTO getSellerId() {
        return sellerId;
    }

    public void setSellerId(UserDTO sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public ProductDTO getTopProductId() {
        return topProductId;
    }

    public void setTopProductId(ProductDTO topProductId) {
        this.topProductId = topProductId;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
