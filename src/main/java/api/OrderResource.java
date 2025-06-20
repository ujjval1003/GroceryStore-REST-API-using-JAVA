/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import EJB.OrderBean;
import Entity.OrderItems;
import Entity.Orders;
import Entity.ProductImages;
import dto.CategoryDTO;
import dto.OrderDTO;
import dto.OrderItemDTO;
import dto.ProductDTO;
import dto.ProductImageDTO;
import dto.UserDTO;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;
import util.Secured;

/**
 *
 * @author ACER
 */
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @EJB
    private OrderBean orderBean;

    public static class UpdateOrderRequest {
        public String status;
        public Long sellerId;
    }

    @GET
    @Path("/user")
    @Secured(role = "USER")
    public Response getOrdersByUser(@Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        try {
            List<Orders> orders = orderBean.getOrdersByUser(Long.valueOf(authUserId));
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(this::toOrderDTO)
                    .collect(Collectors.toList());
            return Response.ok(orderDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Secured(role = "Seller")
    public Response getAllOrders(@Context ContainerRequestContext crequest) {
        try {
            List<Orders> orders = orderBean.getAllOrders();
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(this::toOrderDTO)
                    .collect(Collectors.toList());
            return Response.ok(orderDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{orderId}")
    @Secured(role = "Seller")
    public Response getOrderById(@PathParam("orderId") Long orderId) {
        try {
            Orders order = orderBean.getOrderById(orderId);
            return Response.ok(toOrderDTO(order)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("/{orderId}")
    @Secured(role = "SELLER")
    public Response updateOrderStatus(@PathParam("orderId") Long orderId, @Valid UpdateOrderRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Orders order = orderBean.updateOrderStatus(orderId, request.status, request.sellerId);
            if (order == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
            }
            return Response.ok(toOrderDTO(order)).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation errors: " + violations).build();
        } catch (EJBException e) {
            if (e.getCausedByException() instanceof IllegalArgumentException) {
                String message = e.getCausedByException().getMessage();
                if (message.contains("not authorized to update order status")) {
                    return Response.status(Response.Status.FORBIDDEN).entity(message).build();
                }
                return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
            }
            throw e;
        } catch (PersistenceException e) {
            if (e.getCause() instanceof org.eclipse.persistence.exceptions.DatabaseException &&
                e.getCause().getCause() instanceof java.sql.SQLException &&
                ((java.sql.SQLException) e.getCause().getCause()).getErrorCode() == 1265) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid status value: Data too long or not allowed for status column")
                        .build();
            }
            throw e;
        }
    }

    @PUT
    @Path("/{orderId}/cancel")
    @Secured(role = "USER")
    public Response cancelOrder(@PathParam("orderId") Long orderId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        try {
            Orders order = orderBean.cancelOrder(orderId, Long.valueOf(authUserId));
            if (order == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Order not found").build();
            }
            return Response.ok(toOrderDTO(order)).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation errors: " + violations).build();
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

    private ProductDTO toProductDTO(Entity.Products product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSellerId(new UserDTO(product.getSellerId().getUserId()));
        if (product.getCategoryId() != null) {
            dto.setCategoryId(new CategoryDTO(product.getCategoryId().getCategoryId()));
        }
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        if (product.getProductImagesCollection() != null) {
            dto.setProductImagesCollection(product.getProductImagesCollection().stream()
                    .map(this::toProductImageDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
    
    private ProductImageDTO toProductImageDTO(ProductImages productImage) {
        ProductImageDTO imageDTO = new ProductImageDTO();
        imageDTO.setImageId(productImage.getImageId());
        imageDTO.setImagePath(productImage.getImagePath());
        imageDTO.setCreatedAt(productImage.getCreatedAt());
        return imageDTO;
    }
}