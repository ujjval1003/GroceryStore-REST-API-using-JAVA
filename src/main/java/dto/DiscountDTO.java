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
public class DiscountDTO {
    private Long discountId;
    private UserDTO sellerId;
    private ProductDTO productId;
    private CategoryDTO categoryId;
    private BigDecimal discountPercent;
    private Date startDate;
    private Date endDate;

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public UserDTO getSellerId() {
        return sellerId;
    }

    public void setSellerId(UserDTO sellerId) {
        this.sellerId = sellerId;
    }

    public ProductDTO getProductId() {
        return productId;
    }

    public void setProductId(ProductDTO productId) {
        this.productId = productId;
    }

    public CategoryDTO getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(CategoryDTO categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
