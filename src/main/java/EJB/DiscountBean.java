/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Categories;
import Entity.Discounts;
import Entity.Products;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class DiscountBean {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public Discounts createDiscount(Long sellerId, Long productId, Long categoryId, BigDecimal discountPercent,
                                   Date startDate, Date endDate) {
        if (sellerId == null || discountPercent == null || startDate == null) {
            throw new IllegalArgumentException("Seller ID, discount percent, and start date are required");
        }
        if (discountPercent.compareTo(BigDecimal.ZERO) <= 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100");
        }
        if (endDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        if (productId == null && categoryId == null) {
            throw new IllegalArgumentException("Either product ID or category ID must be provided");
        }

        Users seller = em.find(Users.class, sellerId);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found");
        }
        if (!"SELLER".equals(seller.getRole())) {
            throw new IllegalArgumentException("User must be a seller");
        }

        Products product = productId != null ? em.find(Products.class, productId) : null;
        Categories category = categoryId != null ? em.find(Categories.class, categoryId) : null;

        if (productId != null && product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (categoryId != null && category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        Discounts discount = new Discounts();
        discount.setSellerId(seller);
        discount.setProductId(product);
        discount.setCategoryId(category);
        discount.setDiscountPercent(discountPercent);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);

        em.persist(discount);
        return discount;
    }

    public List<Discounts> getDiscountsBySeller(Long sellerId) {
        if (sellerId == null) {
            throw new IllegalArgumentException("Seller ID is required");
        }
        TypedQuery<Discounts> query = em.createQuery(
            "SELECT d FROM Discounts d WHERE d.sellerId.userId = :sellerId", Discounts.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
}