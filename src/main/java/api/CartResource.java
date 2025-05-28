/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import EJB.CartBean;
import Entity.OrderItems;
import Entity.Orders;
import Entity.ProductImages;
import Entity.Products;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.ProductDTO;
import dto.UserDTO;
import dto.CategoryDTO;
import dto.ProductImageDTO;
import jakarta.ejb.EJB;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import java.util.stream.Collectors;
import util.Secured;

/**
 *
 * @author ACER
 */
@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    @EJB
    private CartBean cartBean;

    public static class CartRequest {
        public Long userId;
        public Long productId;
        public int quantity;
    }

    public static class CheckoutRequest {
        public Long userId;
    }

    public static class DirectCheckoutRequest {
        public Long userId;
        public Long productId;
        public int quantity;
    }
    
    @POST
    @Path("/add")
    @Secured(role = "USER")
    public Response addToCart(CartRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            cartBean.addToCart(request.userId, request.productId, request.quantity);
            return Response.ok("Product added to cart").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/direct-checkout")
    @Secured(role = "USER")
    public Response directCheckout(DirectCheckoutRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            Orders order = cartBean.directCheckout(request.userId, request.productId, request.quantity);
            return Response.status(Response.Status.CREATED).entity(toOrderDTO(order)).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation errors: " + violations).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/checkout")
    @Secured(role = "USER")
    public Response checkoutCart(CheckoutRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            Orders order = cartBean.checkoutCart(request.userId);
            return Response.status(Response.Status.CREATED).entity(toOrderDTO(order)).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation errors: " + violations).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Database error: " + e.getCause().getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    private OrderDTO toOrderDTO(Orders order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(new UserDTO(order.getUserId().getUserId()));
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrderItemsCollection(
                order.getOrderItemsCollection().stream()
                        .map(this::toOrderItemDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private OrderItemDTO toOrderItemDTO(OrderItems item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemId(item.getOrderItemId());
        dto.setProductId(toProductDTO(item.getProductId()));
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setDiscountApplied(item.getDiscountApplied());
        return dto;
    }
    
    private ProductDTO toProductDTO(Products product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSellerId(new UserDTO(product.getSellerId().getUserId()));
        dto.setCategoryId(new CategoryDTO(product.getCategoryId().getCategoryId()));
        dto.setProductImagesCollection(
                product.getProductImagesCollection().stream()
                        .map(this::toProductImageDTO)
                        .collect(Collectors.toList())
        );
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    private ProductImageDTO toProductImageDTO(ProductImages image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setImageId(image.getImageId());
        dto.setImagePath(image.getImagePath());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}