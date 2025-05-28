/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ACER
 */
public class ProductDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private UserDTO sellerId;
    private CategoryDTO categoryId;
    private List<ProductImageDTO> productImagesCollection;
    private Date createdAt;
    private Date updatedAt;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public UserDTO getSellerId() { return sellerId; }
    public void setSellerId(UserDTO sellerId) { this.sellerId = sellerId; }
    public CategoryDTO getCategoryId() { return categoryId; }
    public void setCategoryId(CategoryDTO categoryId) { this.categoryId = categoryId; }
    public List<ProductImageDTO> getProductImagesCollection() { return productImagesCollection; }
    public void setProductImagesCollection(List<ProductImageDTO> productImagesCollection) { this.productImagesCollection = productImagesCollection; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}