/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import EJB.CartBean;
import Entity.CartItems;
import Entity.Carts;
import Entity.OrderItems;
import Entity.Orders;
import Entity.ProductImages;
import Entity.Products;
import dto.CartDTO;
import dto.CartItemDTO;
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

    public static class UpdateCartItemRequest {
        public Long userId;
        public Long productId;
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

    @GET
    @Secured(role = "USER")
    public Response getCartById(@Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        try {
            Carts cart = cartBean.getUserCart(Long.valueOf(authUserId));
            return Response.ok(toCartDTO(cart)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("/increase")
    @Secured(role = "USER")
    public Response increaseCartItemQuantity(UpdateCartItemRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            cartBean.increaseCartItemQuantity(request.userId, request.productId);
            return Response.ok("Cart item quantity increased").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (jakarta.persistence.NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cart item not found").build();
        }
    }

    @PUT
    @Path("/decrease")
    @Secured(role = "USER")
    public Response decreaseCartItemQuantity(UpdateCartItemRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            cartBean.decreaseCartItemQuantity(request.userId, request.productId);
            return Response.ok("Cart item quantity decreased").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (jakarta.persistence.NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Cart item not found").build();
        }
    }
    
    @DELETE
    @Secured(role = "USER")
    @Path("/item/{CartItemId}")
    public Response deleteCartItem(@PathParam("CartItemId") Long CartItemId) {
        cartBean.deleteCartItem(CartItemId);
        return Response.ok("Cart Item deleted").build();
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
    public Response checkoutCart(@Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        try {
            Orders order = cartBean.checkoutCart(Long.valueOf(authUserId));
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
    
    private CartDTO toCartDTO(Carts cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(new UserDTO(cart.getUserId().getUserId()));
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setCartItemsCollection(
                cart.getCartItemsCollection().stream()
                        .map(this::toCartItemDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private CartItemDTO toCartItemDTO(CartItems item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(item.getCartItemId());
        dto.setProductId(toProductDTO(item.getProductId()));
        dto.setQuantity(item.getQuantity());
        return dto;
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