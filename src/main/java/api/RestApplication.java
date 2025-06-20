/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import util.CorsFilter;
import util.JwtFilter;

/**
 *
 * @author ACER
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CartResource.class);
        classes.add(OrderResource.class);
        classes.add(ProductResource.class);
        classes.add(ReviewResource.class);
        classes.add(SellerResource.class);
        classes.add(UserResource.class);
        classes.add(JwtFilter.class);
        classes.add(CorsFilter.class);
        return classes;
    }
}