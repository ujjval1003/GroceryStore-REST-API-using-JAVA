/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import util.Secured;
import EJB.UserBean;
import Entity.Users;
import dto.UserDTO;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.*;

/**
 *
 * @author ACER
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    @EJB
    private UserBean userBean;

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
        public String phone;
        public String address;
        public String role="USER"; // USER or SELLER
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class UpdateProfileRequest {
        public Long userId;
        public String name;
        public String phone;
        public String address;
        public String password;
    }
    
    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        if (request.name == null || request.email == null || request.password == null || request.role == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing required fields").build();
        }
        Users user = userBean.register(request.name, request.email, request.password,
                                      request.phone, request.address, request.role);
        if (user == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Registration failed").build();
        }
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(LoginRequest request) {
        try {
            String token = userBean.login(request.email, request.password);
            return Response.ok(token).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context ContainerRequestContext crequest) {
        String token = crequest.getHeaderString("Authorization").substring("Bearer ".length());
        try {
            userBean.blacklistToken(token);
            return Response.ok("Logged out successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Logout failed").build();
        }
    }
    
    @PUT
    @Secured
    @Path("/profile")
    public Response updateProfile(UpdateProfileRequest request) {
        Users user = userBean.updateProfile(request.userId, request.name, request.phone,
                                           request.address,request.password);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.ok(toUserDTO(user)).build();
    }

    @DELETE
    @Secured
    @Path("/profile/{userId}")
    public Response deleteProfile(@PathParam("userId") Long userId) {
        userBean.deleteProfile(userId);
        return Response.ok("Profile deleted and Logged out").build();
    }
    
    @GET
    @Secured
    @Path("/profile/{userId}")
    public Response getUserProfile(@PathParam("userId") Long userId) {
        Users user = userBean.getUserById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.ok(toUserDTO(user)).build();
    }
    
    private UserDTO toUserDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        return dto;
    }
}