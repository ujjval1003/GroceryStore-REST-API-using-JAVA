/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Orders;
import Entity.Products;
import Entity.Reviews;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class ReviewBean {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public Reviews createReview(Long userId, Long productId, Integer rating, String comment) {
        if (userId == null || productId == null || rating == null) {
            throw new IllegalArgumentException("User ID, Product ID, and rating are required");
        }
        if (rating <= 1 || rating >= 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        Users user = em.find(Users.class, userId);
        Products product = em.find(Products.class, productId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        TypedQuery<Orders> query = em.createQuery(
            "SELECT o FROM Orders o JOIN o.orderItemsCollection oi WHERE o.userId.userId = :userId " +
            "AND oi.productId.productId = :productId AND o.status = 'DELIVERED'", Orders.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        if (query.getResultList().isEmpty()) {
            throw new IllegalArgumentException("User must purchase and receive the product to review it");
        }
            
        Reviews review = new Reviews();
        review.setUserId(user);
        review.setProductId(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(new Date());

        em.persist(review);
        return review;
    }

    public List<Reviews> getReviewsByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        TypedQuery<Reviews> query = em.createQuery(
            "SELECT r FROM Reviews r WHERE r.productId.productId = :productId", Reviews.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
}