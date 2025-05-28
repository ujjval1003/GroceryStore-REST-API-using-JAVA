/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import EJB.AnalyticsBean;
import EJB.CategoryBean;
import EJB.DiscountBean;
import EJB.ProductBean;
import Entity.Categories;
import Entity.Discounts;
import Entity.ProductImages;
import Entity.Products;
import Entity.SellerAnalytics;
import dto.ProductDTO;
import dto.ProductImageDTO;
import dto.SellerAnalyticsDTO;
import dto.UserDTO;
import dto.CategoryDTO;
import dto.DiscountDTO;
import jakarta.ejb.EJB;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import util.Secured;

/**
 *
 * @author ACER
 */
@Path("/seller")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SellerResource {

    @EJB
    private ProductBean productBean;

    @EJB
    private DiscountBean discountBean;

    @EJB
    private AnalyticsBean analyticsBean;
    
    @EJB
    private CategoryBean categoryBean;

    public static class DiscountRequest {
        @NotNull
        public Long sellerId;
        public Long productId;
        public Long categoryId;
        @NotNull
        public BigDecimal discountPercent;
        @NotNull
        public Date startDate;
        public Date endDate;
    }

    public static class AddProductRequest {
        public Long sellerId;
        public String name;
        public String description;
        public BigDecimal price;
        public Integer stockQuantity;
        public Long categoryId;
    }

    public static class UpdateProductRequest {
        public Long sellerId;
        public String name;
        public String description;
        public BigDecimal price;
        public Integer stockQuantity;
        public Long categoryId;
    }

    public static class AddImageRequest {
        public Long sellerId;
        public String imagePath;
    }

    public static class UpdateImageRequest {
        public Long sellerId;
        public String imagePath;
    }

    public static class AddDiscountRequest {
        public Long sellerId;
        public Long productId;
        public Long categoryId;
        public BigDecimal discountPercent;
        public Date startDate;
        public Date endDate;
    }

    public static class AddCategoryRequest {
        public Long sellerId;
        public String name;
        public String description;
    }
    
    public static class UpdateCategoryRequest {
        public Long sellerId;
        public String name;
        public String description;
    }
    
    @POST
    @Path("/products")
    @Secured(role = "SELLER")
    public Response addProduct(AddProductRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Products product = productBean.addProduct(request.sellerId, request.name, request.description,
                                                 request.price, request.stockQuantity, request.categoryId);
            return Response.status(Response.Status.CREATED).entity(toProductDTO(product)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/products/{id}")
    @Secured(role = "SELLER")
    public Response updateProduct(@PathParam("id") Long productId, UpdateProductRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Products product = productBean.updateProduct(productId, request.name, request.description,
                                                    request.price, request.stockQuantity, request.categoryId);
            return Response.ok(toProductDTO(product)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }        
    }

    @DELETE
    @Path("/products/{id}")
    @Secured(role = "SELLER")
    public Response deleteProduct(@PathParam("id") Long productId, @QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            productBean.deleteProduct(productId);
            return Response.ok("Product deleted").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } 
    }

    @POST
    @Path("/products/{id}/images")
    @Secured(role = "SELLER")
    public Response addProductImage(@PathParam("id") Long productId, AddImageRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            ProductImages image = productBean.addProductImage(productId, request.imagePath);
            return Response.status(Response.Status.CREATED).entity(toProductImageDTO(image)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/images/{imageId}")
    @Secured(role = "SELLER")
    public Response updateProductImage(@PathParam("imageId") Long imageId, UpdateImageRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            ProductImages image = productBean.updateProductImage(imageId, request.imagePath);
            return Response.ok(toProductImageDTO(image)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/images/{imageId}")
    @Secured(role = "SELLER")
    public Response deleteProductImage(@PathParam("imageId") Long imageId, @QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            productBean.deleteProductImage(imageId);
            return Response.ok("Image deleted").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/discounts")
    @Secured(role = "SELLER")
    public Response createDiscount(@Valid DiscountRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Discounts discount = discountBean.createDiscount(
                request.sellerId,
                request.productId,
                request.categoryId,
                request.discountPercent,
                request.startDate,
                request.endDate
            );
            return Response.status(Response.Status.CREATED).entity(toDiscountDTO(discount)).build();
        } catch (ConstraintViolationException e) {
            String violations = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Response.status(Response.Status.BAD_REQUEST).entity("Validation errors: " + violations).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/discounts")
    @Secured(role = "SELLER")
    public Response getDiscounts(@QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            List<Discounts> discounts = discountBean.getDiscountsBySeller(sellerId);
            List<DiscountDTO> discountDTOs = discounts.stream()
                    .map(this::toDiscountDTO)
                    .collect(Collectors.toList());
            return Response.ok(discountDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/category")
    @Secured(role = "SELLER")
    public Response addCategory(AddCategoryRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Categories category = categoryBean.addCategory(request.sellerId, request.name, request.description);
            return Response.status(Response.Status.CREATED).entity(toCategoryDTO(category)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/category/{id}")
    @Secured(role = "SELLER")
    public Response updateCategory(@PathParam("id") Long categoryId, UpdateProductRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            Categories category = categoryBean.updateCategory(request.sellerId, categoryId, request.name, request.description);
            return Response.ok(toCategoryDTO(category)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @DELETE
    @Path("/category/{id}")
    @Secured(role = "SELLER")
    public Response deleteCategory(@PathParam("id") Long categoryId, @QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            categoryBean.deleteCategory(categoryId);
            return Response.ok("Product deleted").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/category")
    @Secured(role = "SELLER")
    public Response getAllProducts(@QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        List<Categories> category = categoryBean.getAllCategories();
        List<CategoryDTO> categoryDTOs = category.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
        return Response.ok(categoryDTOs).build();
    }
    
    @GET
    @Path("/analytics")
    @Secured(role = "SELLER")
    public Response getAnalytics(@QueryParam("sellerId") Long sellerId, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(sellerId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized seller ID").build();
        }
        try {
            SellerAnalytics analytics = analyticsBean.getAnalytics(sellerId);
            return Response.ok(toSellerAnalyticsDTO(analytics)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private SellerAnalyticsDTO toSellerAnalyticsDTO(SellerAnalytics analytics) {
        SellerAnalyticsDTO dto = new SellerAnalyticsDTO();
        dto.setAnalyticId(analytics.getAnalyticId());
        dto.setSellerId(new UserDTO(analytics.getSellerId().getUserId()));
        dto.setTotalSales(analytics.getTotalSales());
        dto.setTotalOrders(analytics.getTotalOrders());
        dto.setTopProductId(analytics.getTopProductId() != null ? toProductDTO(analytics.getTopProductId()) : null);
        dto.setLastUpdated(analytics.getLastUpdated());
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
    
    private CategoryDTO toCategoryDTO(Categories category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private ProductImageDTO toProductImageDTO(ProductImages image) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setImageId(image.getImageId());
        dto.setImagePath(image.getImagePath());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
    
    private DiscountDTO toDiscountDTO(Discounts discount) {
        DiscountDTO dto = new DiscountDTO();
        dto.setDiscountId(discount.getDiscountId());
        UserDTO sellerDTO = new UserDTO();
        sellerDTO.setUserId(discount.getSellerId().getUserId());
        dto.setSellerId(sellerDTO);
        if (discount.getProductId() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(discount.getProductId().getProductId());
            productDTO.setName(discount.getProductId().getName());
            productDTO.setPrice(discount.getProductId().getPrice());
            dto.setProductId(productDTO);
        }
        if (discount.getCategoryId() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryId(discount.getCategoryId().getCategoryId());
            dto.setCategoryId(categoryDTO);
        }
        dto.setDiscountPercent(discount.getDiscountPercent());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        return dto;
    }
}