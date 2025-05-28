/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import EJB.ReviewBean;
import Entity.Reviews;
import dto.ProductDTO;
import dto.ReviewDTO;
import dto.UserDTO;
import jakarta.ejb.EJB;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    @EJB
    private ReviewBean reviewBean;

    public static class ReviewRequest {
        @NotNull
        public Long userId;
        @NotNull
        public Long productId;
        @NotNull
        public Integer rating;
        public String comment;
    }

    @POST
    @Secured(role = "USER")
    public Response createReview(@Valid ReviewRequest request, @Context ContainerRequestContext crequest) {
        String authUserId = (String) crequest.getProperty("userId");
        if (!authUserId.equals(String.valueOf(request.userId))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Unauthorized user ID").build();
        }
        try {
            Reviews review = reviewBean.createReview(
                request.userId,
                request.productId,
                request.rating,
                request.comment
            );
            return Response.status(Response.Status.CREATED).entity(toReviewDTO(review)).build();
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
    public Response getReviews(@QueryParam("productId") Long productId) {
        try {
            List<Reviews> reviews = reviewBean.getReviewsByProduct(productId);
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(this::toReviewDTO)
                    .collect(Collectors.toList());
            return Response.ok(reviewDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    private ReviewDTO toReviewDTO(Reviews review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(review.getProductId().getProductId());
        productDTO.setName(review.getProductId().getName());
        productDTO.setPrice(review.getProductId().getPrice());
        dto.setProductId(productDTO);
        dto.setUserId(new UserDTO(review.getUserId().getUserId()));
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}