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
@Table(name = "seller_analytics")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SellerAnalytics.findAll", query = "SELECT s FROM SellerAnalytics s"),
    @NamedQuery(name = "SellerAnalytics.findByAnalyticId", query = "SELECT s FROM SellerAnalytics s WHERE s.analyticId = :analyticId"),
    @NamedQuery(name = "SellerAnalytics.findByTotalSales", query = "SELECT s FROM SellerAnalytics s WHERE s.totalSales = :totalSales"),
    @NamedQuery(name = "SellerAnalytics.findByTotalOrders", query = "SELECT s FROM SellerAnalytics s WHERE s.totalOrders = :totalOrders"),
    @NamedQuery(name = "SellerAnalytics.findByLastUpdated", query = "SELECT s FROM SellerAnalytics s WHERE s.lastUpdated = :lastUpdated")})
public class SellerAnalytics implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "analytic_id")
    private Long analyticId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_sales")
    private BigDecimal totalSales;
    @Column(name = "total_orders")
    private Integer totalOrders;
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Users sellerId;
    @JoinColumn(name = "top_product_id", referencedColumnName = "product_id")
    @ManyToOne
    private Products topProductId;

    public SellerAnalytics() {
    }

    public SellerAnalytics(Long analyticId) {
        this.analyticId = analyticId;
    }

    public SellerAnalytics(Long analyticId, Date lastUpdated) {
        this.analyticId = analyticId;
        this.lastUpdated = lastUpdated;
    }

    public Long getAnalyticId() {
        return analyticId;
    }

    public void setAnalyticId(Long analyticId) {
        this.analyticId = analyticId;
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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Users getSellerId() {
        return sellerId;
    }

    public void setSellerId(Users sellerId) {
        this.sellerId = sellerId;
    }

    public Products getTopProductId() {
        return topProductId;
    }

    public void setTopProductId(Products topProductId) {
        this.topProductId = topProductId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (analyticId != null ? analyticId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SellerAnalytics)) {
            return false;
        }
        SellerAnalytics other = (SellerAnalytics) object;
        if ((this.analyticId == null && other.analyticId != null) || (this.analyticId != null && !this.analyticId.equals(other.analyticId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.SellerAnalytics[ analyticId=" + analyticId + " ]";
    }
    
}
