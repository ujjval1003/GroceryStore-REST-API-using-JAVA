/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "discounts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Discounts.findAll", query = "SELECT d FROM Discounts d"),
    @NamedQuery(name = "Discounts.findByDiscountId", query = "SELECT d FROM Discounts d WHERE d.discountId = :discountId"),
    @NamedQuery(name = "Discounts.findByDiscountPercent", query = "SELECT d FROM Discounts d WHERE d.discountPercent = :discountPercent"),
    @NamedQuery(name = "Discounts.findByStartDate", query = "SELECT d FROM Discounts d WHERE d.startDate = :startDate"),
    @NamedQuery(name = "Discounts.findByEndDate", query = "SELECT d FROM Discounts d WHERE d.endDate = :endDate")})
public class Discounts implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "discount_id")
    private Long discountId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "discount_percent")
    private BigDecimal discountPercent;
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users sellerId;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ManyToOne
    private Categories categoryId;
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ManyToOne
    private Products productId;

    public Discounts() {
    }

    public Discounts(Long discountId) {
        this.discountId = discountId;
    }

    public Discounts(Long discountId, BigDecimal discountPercent) {
        this.discountId = discountId;
        this.discountPercent = discountPercent;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
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

    public Products getProductId() {
        return productId;
    }

    public void setProductId(Products productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (discountId != null ? discountId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Discounts)) {
            return false;
        }
        Discounts other = (Discounts) object;
        if ((this.discountId == null && other.discountId != null) || (this.discountId != null && !this.discountId.equals(other.discountId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Discounts[ discountId=" + discountId + " ]";
    }
    
}
