/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "products")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Products.findAll", query = "SELECT p FROM Products p"),
    @NamedQuery(name = "Products.findByProductId", query = "SELECT p FROM Products p WHERE p.productId = :productId"),
    @NamedQuery(name = "Products.findByName", query = "SELECT p FROM Products p WHERE p.name = :name"),
    @NamedQuery(name = "Products.findByPrice", query = "SELECT p FROM Products p WHERE p.price = :price"),
    @NamedQuery(name = "Products.findByStockQuantity", query = "SELECT p FROM Products p WHERE p.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "Products.findByCreatedAt", query = "SELECT p FROM Products p WHERE p.createdAt = :createdAt"),
    @NamedQuery(name = "Products.findByUpdatedAt", query = "SELECT p FROM Products p WHERE p.updatedAt = :updatedAt")})
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "product_id")
    private Long productId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_quantity")
    private int stockQuantity;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<ProductImages> productImagesCollection;
    @OneToMany(mappedBy = "topProductId")
    private Collection<SellerAnalytics> sellerAnalyticsCollection;
    @OneToMany(mappedBy = "productId")
    private Collection<Discounts> discountsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<Reviews> reviewsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<CartItems> cartItemsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productId")
    private Collection<OrderItems> orderItemsCollection;
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users sellerId;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne
    private Categories categoryId;

    public Products() {
    }

    public Products(Long productId) {
        this.productId = productId;
    }

    public Products(Long productId, String name, BigDecimal price, int stockQuantity, Date createdAt, Date updatedAt) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @XmlTransient
    public Collection<ProductImages> getProductImagesCollection() {
        return productImagesCollection;
    }

    public void setProductImagesCollection(Collection<ProductImages> productImagesCollection) {
        this.productImagesCollection = productImagesCollection;
    }

    @XmlTransient
    public Collection<SellerAnalytics> getSellerAnalyticsCollection() {
        return sellerAnalyticsCollection;
    }

    public void setSellerAnalyticsCollection(Collection<SellerAnalytics> sellerAnalyticsCollection) {
        this.sellerAnalyticsCollection = sellerAnalyticsCollection;
    }

    @XmlTransient
    public Collection<Discounts> getDiscountsCollection() {
        return discountsCollection;
    }

    public void setDiscountsCollection(Collection<Discounts> discountsCollection) {
        this.discountsCollection = discountsCollection;
    }

    @XmlTransient
    public Collection<Reviews> getReviewsCollection() {
        return reviewsCollection;
    }

    public void setReviewsCollection(Collection<Reviews> reviewsCollection) {
        this.reviewsCollection = reviewsCollection;
    }

    @XmlTransient
    public Collection<CartItems> getCartItemsCollection() {
        return cartItemsCollection;
    }

    public void setCartItemsCollection(Collection<CartItems> cartItemsCollection) {
        this.cartItemsCollection = cartItemsCollection;
    }

    @XmlTransient
    public Collection<OrderItems> getOrderItemsCollection() {
        return orderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItems> orderItemsCollection) {
        this.orderItemsCollection = orderItemsCollection;
    }

    public Users getSellerId() {
        return sellerId;
    }

    public void setSellerId(Users sellerId) {
        this.sellerId = sellerId;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productId != null ? productId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Products)) {
            return false;
        }
        Products other = (Products) object;
        if ((this.productId == null && other.productId != null) || (this.productId != null && !this.productId.equals(other.productId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Products[ productId=" + productId + " ]";
    }
    
}
