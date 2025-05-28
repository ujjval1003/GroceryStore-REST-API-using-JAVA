/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import dto.CategoryDTO;
import dto.ProductDTO;
import dto.ProductImageDTO;
import dto.UserDTO;
import EJB.ProductBean;
import Entity.ProductImages;
import Entity.Products;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ACER
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @EJB
    private ProductBean productBean;

    @GET
    public Response getAllProducts() {
        List<Products> products = productBean.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return Response.ok(productDTOs).build();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long productId) {
        if (productId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Product ID required").build();
        }
        Products product = productBean.getProductById(productId);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Product not found").build();
        }
        return Response.ok(toDTO(product)).build();
    }

    @GET
    @Path("/search")
    public Response searchProducts(@QueryParam("keyword") String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Keyword required").build();
        }
        List<Products> products = productBean.searchProducts(keyword);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return Response.ok(productDTOs).build();
    }

    @GET
    @Path("/filter")
    public Response filterProducts(
            @QueryParam("categoryId") Long categoryId,
            @QueryParam("minPrice") BigDecimal minPrice,
            @QueryParam("maxPrice") BigDecimal maxPrice) {
        List<Products> products = productBean.filterProducts(categoryId, minPrice, maxPrice);
        List<ProductDTO> productDTOs = products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return Response.ok(productDTOs).build();
    }

    private ProductDTO toDTO(Products product) {
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