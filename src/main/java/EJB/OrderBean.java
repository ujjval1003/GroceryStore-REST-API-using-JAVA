/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Orders;
import Entity.Users;
import jakarta.ejb.Stateless;
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
public class OrderBean {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    // Retrieve orders for a user
    public List<Orders> getOrdersByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        TypedQuery<Orders> query = em.createQuery(
                "SELECT o FROM Orders o WHERE o.userId.userId = :userId ORDER BY o.createdAt DESC", Orders.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public Orders getOrderById (Long orderId) {
        TypedQuery<Orders> query = em.createQuery(
                "SELECT o FROM Orders o WHERE o.orderId = :orderId ORDER BY o.createdAt DESC", Orders.class);
        query.setParameter("userId", orderId);
        return (Orders) query;
    }

    // Update order status (for sellers)
    public Orders updateOrderStatus(Long orderId, String status, Long sellerId) {
        Users seller = em.find(Users.class, sellerId);
        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return null;
        }
        
        String orderStatus = status.trim().toUpperCase();

        Orders order = em.find(Orders.class, orderId);
        if (order == null) {
            return null;
        }
        order.setStatus(orderStatus);
        order.setUpdatedAt(new Date());
        em.merge(order);
        return order;
    }

    // Cancel an order (by user or admin)
    public Orders cancelOrder(Long orderId, Long userId) {
        Users user = em.find(Users.class, userId);
        if (user == null || !"USER".equals(user.getRole())) {
            return null;
        }

        Orders order = em.find(Orders.class, orderId);
        if (order == null) {
            return null;
        }

        // Check if user is the order owner or an admin
        if (!order.getUserId().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the order owner can cancel the order");
        }

        // Check if order can be cancelled
        if (List.of("SHIPPED", "DELIVERED").contains(order.getStatus().toUpperCase())) {
            throw new IllegalArgumentException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        order.setUpdatedAt(new Date());

        // Restore product stock
        order.getOrderItemsCollection().forEach(item -> {
            item.getProductId().setStockQuantity(
                    item.getProductId().getStockQuantity() + item.getQuantity()
            );
            item.getProductId().setUpdatedAt(new Date());
            em.merge(item.getProductId());
        });

        em.merge(order);
        return order;
    }

    public List<Orders> getAllOrders() {
        TypedQuery<Orders> query = em.createQuery(
                "SELECT o FROM Orders o ORDER BY o.createdAt DESC", Orders.class);
        return query.getResultList();
    }
}