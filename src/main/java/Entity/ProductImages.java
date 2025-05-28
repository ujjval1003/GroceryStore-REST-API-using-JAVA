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
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "product_images")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductImages.findAll", query = "SELECT p FROM ProductImages p"),
    @NamedQuery(name = "ProductImages.findByImageId", query = "SELECT p FROM ProductImages p WHERE p.imageId = :imageId"),
    @NamedQuery(name = "ProductImages.findByImagePath", query = "SELECT p FROM ProductImages p WHERE p.imagePath = :imagePath"),
    @NamedQuery(name = "ProductImages.findByCreatedAt", query = "SELECT p FROM ProductImages p WHERE p.createdAt = :createdAt")})
public class ProductImages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "image_id")
    private Long imageId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "image_path")
    private String imagePath;
    @Basic(optional = false)
    @NotNull
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ManyToOne(optional = false)
    private Products productId;

    public ProductImages() {
    }

    public ProductImages(Long imageId) {
        this.imageId = imageId;
    }

    public ProductImages(Long imageId, String imagePath, Date createdAt) {
        this.imageId = imageId;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
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
        hash += (imageId != null ? imageId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductImages)) {
            return false;
        }
        ProductImages other = (ProductImages) object;
        if ((this.imageId == null && other.imageId != null) || (this.imageId != null && !this.imageId.equals(other.imageId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.ProductImages[ imageId=" + imageId + " ]";
    }
    
}
