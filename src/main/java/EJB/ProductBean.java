/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Categories;
import Entity.ProductImages;
import Entity.Products;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class ProductBean {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    // Add a product
    public Products addProduct(Long sellerId, String name, String description, BigDecimal price, Integer stockQuantity, Long categoryId) {
        Users seller = em.find(Users.class, sellerId);
        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return null;
        }
        Products product = new Products();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setSellerId(seller);
        if (categoryId != null) {
            Categories category = em.find(Categories.class, categoryId);
            if (category != null) {
                product.setCategoryId(category);
            }
        }
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        em.persist(product);
        return product;
    }
    
    // Add a product image
    public ProductImages addProductImage(Long productId, String imagePath) {
        Products product = em.find(Products.class, productId);
        if (product == null) {
            return null;
        }
        ProductImages image = new ProductImages();
        image.setProductId(product);
        image.setImagePath(imagePath);
        image.setCreatedAt(new Date());
        em.persist(image);
        return image;
    }

    // Update a product
    public Products updateProduct(Long productId, String name, String description, BigDecimal price, int stock, Long categoryId) {
        Products product = em.find(Products.class, productId);
        if (product != null) {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStockQuantity(stock);
            product.setCategoryId(categoryId != null ? em.find(Categories.class, categoryId) : null);
            em.merge(product);
        }
        return product;
    }

    // Delete a product
    public void deleteProduct(Long productId) {
        Products product = em.find(Products.class, productId);
        if (product != null) {
            em.remove(product);
        }
    }

    // Search products
    public List<Products> searchProducts(String keyword) {
        TypedQuery<Products> query = em.createQuery(
            "SELECT p FROM Products p WHERE p.name LIKE :keyword OR p.description LIKE :keyword", Products.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }
    
    // Filter products
    public List<Products> filterProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Products p WHERE 1=1");
        if (categoryId != null) jpql.append(" AND p.categoryId.categoryId = :categoryId");
        if (minPrice != null) jpql.append(" AND p.price >= :minPrice");
        if (maxPrice != null) jpql.append(" AND p.price <= :maxPrice");
        TypedQuery<Products> query = em.createQuery(jpql.toString(), Products.class);
        if (categoryId != null) query.setParameter("categoryId", categoryId);
        if (minPrice != null) query.setParameter("minPrice", minPrice);
        if (maxPrice != null) query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }

    // List all products
    public List<Products> getAllProducts() {
        return em.createQuery("SELECT p FROM Products p LEFT JOIN FETCH p.productImagesCollection", Products.class)
                 .getResultList();
    }

    // Update a product image
    public ProductImages updateProductImage(Long imageId, String imagePath) {
        ProductImages image = em.find(ProductImages.class, imageId);
        if (image != null) {
            image.setImagePath(imagePath);
            em.merge(image);
            return image;
        }
        return null;
    }

    // Delete a product image
    public boolean deleteProductImage(Long imageId) {
        ProductImages image = em.find(ProductImages.class, imageId);
        if (image != null) {
            em.remove(image);
            return true;
        }
        return false;
    }
    
    public Products getProductById(Long productId) {
        return em.find(Products.class, productId);
    }
}