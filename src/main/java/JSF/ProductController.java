/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JSF;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import EJB.CartBean;
import dto.CategoryDTO;
import dto.ProductDTO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ACER
 */
@Named
@ViewScoped
public class ProductController implements Serializable {
    private static final String API_BASE_URL = "http://localhost:8080/GroceryStore/api";
    private List<ProductDTO> products;
    private List<CategoryDTO> categories;
    private ProductDTO selectedProduct;
    private boolean showQuickView;
    private Long selectedCategoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @Inject
    private UserController userController;

    @Inject
    private CartBean cartBean;

    @PostConstruct
    public void init() {
        loadProducts();
        loadCategories();
    }

    public void loadProducts() {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(API_BASE_URL + "/products")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                products = response.readEntity(new jakarta.ws.rs.core.GenericType<List<ProductDTO>>() {});
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load products", null));
                products = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading products: " + e.getMessage(), null));
            products = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    public void loadCategories() {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(API_BASE_URL + "/products/category")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                categories = response.readEntity(new jakarta.ws.rs.core.GenericType<List<CategoryDTO>>() {});
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load categories", null));
                categories = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading categories: " + e.getMessage(), null));
            categories = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    public void filterProducts() {
        Client client = ClientBuilder.newClient();
        try {
            StringBuilder queryParams = new StringBuilder();
            if (selectedCategoryId != null) {
                queryParams.append("categoryId=").append(selectedCategoryId).append("&");
            }
            if (minPrice != null) {
                queryParams.append("minPrice=").append(minPrice).append("&");
            }
            if (maxPrice != null) {
                queryParams.append("maxPrice=").append(maxPrice).append("&");
            }
            String queryString = queryParams.toString();
            if (!queryString.isEmpty()) {
                queryString = "?" + queryString.substring(0, queryString.length() - 1);
            }
            Response response = client.target(API_BASE_URL + "/products/filter" + queryString)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                products = response.readEntity(new jakarta.ws.rs.core.GenericType<List<ProductDTO>>() {});
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to filter products", null));
                products = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error filtering products: " + e.getMessage(), null));
            products = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    public void filterByCategory(Long categoryId) {
        this.selectedCategoryId = categoryId;
        filterProducts();
    }

    public String addToCart(Long productId) {
        if (!userController.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Please log in to add to cart", null));
            return "login.jsf?faces-redirect=true";
        }

        try {
            Long userId = userController.getUserId();
            cartBean.addToCart(userId, productId, 1);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Product added to cart!", null));
        } catch (IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
        return null;
    }

    // Getters and Setters
    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }
    public ProductDTO getSelectedProduct() { return selectedProduct; }
    public void setSelectedProduct(ProductDTO selectedProduct) { this.selectedProduct = selectedProduct; }
    public boolean isShowQuickView() { return showQuickView; }
    public void setShowQuickView(boolean showQuickView) { this.showQuickView = showQuickView; }
    public Long getSelectedCategoryId() { return selectedCategoryId; }
    public void setSelectedCategoryId(Long selectedCategoryId) { this.selectedCategoryId = selectedCategoryId; }
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
}