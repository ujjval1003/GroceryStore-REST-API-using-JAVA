/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Products;
import Entity.SellerAnalytics;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class AnalyticsBean {
    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    // Update seller analytics (call after order completion)
    public void updateAnalytics(Long sellerId) {
        TypedQuery<SellerAnalytics> query = em.createQuery(
            "SELECT sa FROM SellerAnalytics sa WHERE sa.sellerId.userId = :sellerId", SellerAnalytics.class);
        query.setParameter("sellerId", sellerId);
        SellerAnalytics analytics = query.getResultList().stream().findFirst().orElse(null);
        if (analytics == null) {
            analytics = new SellerAnalytics();
            analytics.setSellerId(em.find(Users.class, sellerId));
            em.persist(analytics);
        }

        Query salesQuery = em.createQuery(
            "SELECT SUM(oi.priceAtPurchase * oi.quantity) FROM OrderItems oi " +
            "WHERE oi.productId.sellerId.userId = :sellerId");
        salesQuery.setParameter("sellerId", sellerId);
        Double totalSalesDouble = (Double) salesQuery.getSingleResult();
        BigDecimal totalSales = totalSalesDouble != null ? BigDecimal.valueOf(totalSalesDouble) : BigDecimal.ZERO;
        analytics.setTotalSales(totalSales);

        Query ordersQuery = em.createQuery(
            "SELECT COUNT(DISTINCT o.orderId) FROM Orders o JOIN o.orderItems oi " +
            "WHERE oi.productId.sellerId.userId = :sellerId");
        ordersQuery.setParameter("sellerId", sellerId);
        Long totalOrders = (Long) ordersQuery.getSingleResult();
        analytics.setTotalOrders(totalOrders != null ? totalOrders.intValue() : 0);

        Query topProductQuery = em.createQuery(
            "SELECT oi.productId.productId FROM OrderItems oi " +
            "WHERE oi.productId.sellerId.userId = :sellerId " +
            "GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC");
        topProductQuery.setParameter("sellerId", sellerId);
        topProductQuery.setMaxResults(1);
        Long topProductId = (Long) topProductQuery.getSingleResult();
        analytics.setTopProductId(topProductId != null ? em.find(Products.class, topProductId) : null);
        analytics.setLastUpdated(new Date());
        em.merge(analytics);
    }

    // Get analytics for seller
    public SellerAnalytics getAnalytics(Long sellerId) {
        TypedQuery<SellerAnalytics> query = em.createQuery(
            "SELECT sa FROM SellerAnalytics sa WHERE sa.sellerId.userId = :sellerId", SellerAnalytics.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList().stream().findFirst().orElse(null);
    }
}