/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.CartItems;
import Entity.Carts;
import Entity.Discounts;
import Entity.OrderItems;
import Entity.Orders;
import Entity.Products;
import Entity.Users;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ACER
 */
@Stateless
@LocalBean
public class CartBean {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;
    
    @EJB
    private ProductBean productBean;

    @EJB
    private UserBean userBean;

    // Helper method to get the highest applicable discount for a product
    private Discounts getApplicableDiscount(Long productId, Long categoryId) {
        Date now = new Date();
        TypedQuery<Discounts> query = em.createQuery(
            "SELECT d FROM Discounts d WHERE ((d.productId.productId = :productId) OR " +
            "(d.categoryId.categoryId = :categoryId AND :categoryId IS NOT NULL)) " +
            "AND d.startDate <= :now AND (d.endDate IS NULL OR d.endDate >= :now)", Discounts.class);
        query.setParameter("productId", productId);
        query.setParameter("categoryId", categoryId);
        query.setParameter("now", now);
        List<Discounts> discounts = query.getResultList();

        // Return the discount with the highest discountPercent
        return discounts.stream()
            .max((d1, d2) -> d1.getDiscountPercent().compareTo(d2.getDiscountPercent()))
            .orElse(null);
    }

    // Get or create cart for user
    public Carts getUserCart(Long userId) {
        TypedQuery<Carts> query = em.createQuery(
            "SELECT c FROM Carts c WHERE c.userId.userId = :userId", Carts.class);
        query.setParameter("userId", userId);
        List<Carts> carts = query.getResultList();
        if (carts.isEmpty()) {
            Carts cart = new Carts();
            cart.setUserId(em.find(Users.class, userId));
            cart.setCreatedAt(new Date());
            em.persist(cart);
            return cart;
        }
        Carts cart = carts.get(0);
        // Initialize cart items collection to avoid LazyInitializationException
        cart.getCartItemsCollection().size();
        return cart;
    }

    // Add product to cart
    public void addToCart(Long userId, Long productId, int quantity) {
        Carts cart = getUserCart(userId);
        Products product = em.find(Products.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        TypedQuery<CartItems> query = em.createQuery(
            "SELECT ci FROM CartItems ci WHERE ci.cartId.cartId = :cartId AND ci.productId.productId = :productId",
            CartItems.class
        );
        query.setParameter("cartId", cart.getCartId());
        query.setParameter("productId", productId);

        List<CartItems> existingItems = query.getResultList();
        if (!existingItems.isEmpty()) {
            // Update existing entry
            CartItems existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            em.merge(existingItem);
        } else {
            // Create new entry
            CartItems item = new CartItems();
            item.setCartId(cart);
            item.setProductId(product);
            item.setQuantity(quantity);
            em.persist(item);
        }
    }

    // Increase cart item quantity
    public void increaseCartItemQuantity(Long userId, Long productId) {
        Carts cart = getUserCart(userId);
        TypedQuery<CartItems> query = em.createQuery(
            "SELECT ci FROM CartItems ci WHERE ci.cartId.cartId = :cartId AND ci.productId.productId = :productId",
            CartItems.class
        );
        query.setParameter("cartId", cart.getCartId());
        query.setParameter("productId", productId);

        CartItems cartItem = query.getSingleResult();
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        em.merge(cartItem);
    }

    // Decrease cart item quantity
    public void decreaseCartItemQuantity(Long userId, Long productId) {
        Carts cart = getUserCart(userId);
        TypedQuery<CartItems> query = em.createQuery(
            "SELECT ci FROM CartItems ci WHERE ci.cartId.cartId = :cartId AND ci.productId.productId = :productId",
            CartItems.class
        );
        query.setParameter("cartId", cart.getCartId());
        query.setParameter("productId", productId);
        CartItems cartItem = query.getSingleResult();
        
        if (cartItem.getQuantity() <= 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        cartItem.setQuantity(cartItem.getQuantity() - 1);
        em.merge(cartItem);
    }
    
    // Direct checkout (single product)
    public Orders directCheckout(Long userId, Long productId, int quantity) {
        // Validate inputs
        if (userId == null || productId == null || quantity <= 0) {
            throw new IllegalArgumentException("User ID, Product ID, and quantity must be provided and valid");
        }

        // Fetch user and product
        Users user = userBean.getUserById(userId);
        Products product = productBean.getProductById(productId);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        // Create Orders entity
        Date now = new Date();
        Orders order = new Orders();
        order.setUserId(user);
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus("PENDING"); // Must be 1-10 characters
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // Create OrderItems
        OrderItems orderItem = new OrderItems();
        orderItem.setOrderId(order);
        orderItem.setProductId(product);
        orderItem.setQuantity(quantity);
        orderItem.setPriceAtPurchase(product.getPrice());
        orderItem.setDiscountApplied(BigDecimal.ZERO);

        List<OrderItems> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItemsCollection(orderItems);

        // Update product stock
        product.setStockQuantity(product.getStockQuantity() - quantity);
        em.merge(product);

        // Persist order
        em.persist(order);
        em.flush();

        // Persist order item
        em.persist(orderItem);

        return order;
    }

    // Checkout entire cart
    public Orders checkoutCart(Long userId) {
        Carts cart = getUserCart(userId);
        TypedQuery<CartItems> query = em.createQuery(
            "SELECT ci FROM CartItems ci WHERE ci.cartId.cartId = :cartId", CartItems.class);
        query.setParameter("cartId", cart.getCartId());
        List<CartItems> items = query.getResultList();

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Date now = new Date();
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItems> orderItems = new ArrayList<>();

        // Calculate total price and prepare order items
        for (CartItems item : items) {
            Products product = item.getProductId();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            BigDecimal priceAtPurchase = product.getPrice();
            BigDecimal itemTotal = priceAtPurchase.multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal discountApplied = BigDecimal.ZERO;

            // Check for applicable discount
            Discounts discount = getApplicableDiscount(
                product.getProductId(),
                product.getCategoryId() != null ? product.getCategoryId().getCategoryId() : null);
            if (discount != null) {
                BigDecimal discountPercent = discount.getDiscountPercent();
                discountApplied = itemTotal.multiply(discountPercent)
                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            }

            // Create order item
            OrderItems orderItem = new OrderItems();
            orderItem.setProductId(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(priceAtPurchase);
            orderItem.setDiscountApplied(discountApplied);
            orderItems.add(orderItem);

            // Update total price
            totalPrice = totalPrice.add(itemTotal.subtract(discountApplied));
        }

        // Create and persist Orders entity
        Orders order = new Orders();
        order.setUserId(em.find(Users.class, userId));
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        order.setOrderItemsCollection(new ArrayList<>());
        em.persist(order);
        em.flush(); // Ensure orderId is generated

        // Persist order items and update stock
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItems orderItem = orderItems.get(i);
            CartItems cartItem = items.get(i);
            orderItem.setOrderId(order);
            em.persist(orderItem);

            // Update the collection
            order.getOrderItemsCollection().add(orderItem);

            Products product = cartItem.getProductId();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setUpdatedAt(now);
            em.merge(product);
            em.remove(cartItem);
        }
        return order;
    }
    
    public void deleteCartItem(Long CartItemId) {
        CartItems item = em.find(CartItems.class, CartItemId);
        em.remove(item);
    }
}