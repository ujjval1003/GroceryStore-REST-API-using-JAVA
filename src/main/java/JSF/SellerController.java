/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JSF;

import api.OrderResource;
import api.SellerResource;
import dto.CategoryDTO;
import dto.OrderDTO;
import dto.ProductDTO;
import dto.ProductImageDTO;
import dto.SellerAnalyticsDTO;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ACER
 */
@Named
@SessionScoped
public class SellerController implements Serializable {
    private static final String API_BASE_URL = "http://localhost:8080/GroceryStore/api/seller";
    private static final String UPLOAD_DIR = "/uploads/images/";
    private String token; // JWT from UserController
    private Long sellerId;
    private String role;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer productStockQuantity;
    private Long productCategoryId;
    private Long productId;
    private Part imageFile;
    private Long imageId;
    private String categoryName;
    private String categoryDescription;
    private Long categoryId;
    private List<OrderDTO> orders;
    private String status;
    private SellerAnalyticsDTO analytics;
    private List<CategoryDTO> categories;

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }
    public Integer getProductStockQuantity() { return productStockQuantity; }
    public void setProductStockQuantity(Integer productStockQuantity) { this.productStockQuantity = productStockQuantity; }
    public Long getProductCategoryId() { return productCategoryId; }
    public void setProductCategoryId(Long productCategoryId) { this.productCategoryId = productCategoryId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Part getImageFile() { return imageFile; }
    public void setImageFile(Part imageFile) { this.imageFile = imageFile; }
    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryDescription() { return categoryDescription; }
    public void setCategoryDescription(String categoryDescription) { this.categoryDescription = categoryDescription; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public List<OrderDTO> getOrders() { return orders; }
    public void setOrders(List<OrderDTO> orders) { this.orders = orders; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public SellerAnalyticsDTO getAnalytics() { return analytics; }
    public void setAnalytics(SellerAnalyticsDTO analytics) { this.analytics = analytics; }
    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }

    private Client getClient() {
        return ClientBuilder.newClient();
    }

    private String saveImageFile(Part file) throws IOException {
        if (file == null || file.getSize() == 0) {
            return null;
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getSubmittedFileName();
        String uploadPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("") + UPLOAD_DIR;
        Path directory = Paths.get(uploadPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        File outputFile = new File(uploadPath, fileName);
        try (InputStream input = file.getInputStream();
             FileOutputStream output = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
        return UPLOAD_DIR + fileName;
    }

    public String addProduct() {
        if (!"SELLER".equals(role)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Only sellers can add products", null));
            return "login.jsf?faces-redirect=true";
        }
        Client client = getClient();
        try {
            // Add product
            SellerResource.AddProductRequest request = new SellerResource.AddProductRequest();
            request.sellerId = sellerId;
            request.name = productName;
            request.description = productDescription;
            request.price = productPrice;
            request.stockQuantity = productStockQuantity;
            request.categoryId = productCategoryId;

            Response productResponse = client.target(API_BASE_URL + "/products")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .post(Entity.json(request));

            if (productResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to add product", null));
                return null;
            }

            ProductDTO product = productResponse.readEntity(ProductDTO.class);
            Long newProductId = product.getProductId();

            // Add product image if provided
            String imagePath = saveImageFile(imageFile);
            if (imagePath != null) {
                SellerResource.AddImageRequest imageRequest = new SellerResource.AddImageRequest();
                imageRequest.sellerId = sellerId;
                imageRequest.imagePath = imagePath;

                Response imageResponse = client.target(API_BASE_URL + "/products/" + newProductId + "/images")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .post(Entity.json(imageRequest));

                if (imageResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Product added, but failed to add image", null));
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product added successfully!"));
            clearProductFields();
            return "sellerproducts.jsf?faces-redirect=true";
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Image upload failed: " + e.getMessage(), null));
            return null;
        } finally {
            client.close();
        }
    }

    public String updateProduct() {
        if (!"SELLER".equals(role)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Only sellers can update products", null));
            return "login.jsf?faces-redirect=true";
        }
        Client client = getClient();
        try {
            // Update product
            SellerResource.UpdateProductRequest request = new SellerResource.UpdateProductRequest();
            request.sellerId = sellerId;
            request.name = productName;
            request.description = productDescription;
            request.price = productPrice;
            request.stockQuantity = productStockQuantity;
            request.categoryId = productCategoryId;

            Response productResponse = client.target(API_BASE_URL + "/products/" + productId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .put(Entity.json(request));

            if (productResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update product", null));
                return null;
            }

            // Update product image if provided
            if (imageFile != null && imageFile.getSize() > 0) {
                String imagePath = saveImageFile(imageFile);
                if (imagePath != null) {
                    SellerResource.UpdateImageRequest imageRequest = new SellerResource.UpdateImageRequest();
                    imageRequest.sellerId = sellerId;
                    imageRequest.imagePath = imagePath;

                    Response imageResponse = client.target(API_BASE_URL + "/images/" + imageId)
                            .request(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .put(Entity.json(imageRequest));

                    if (imageResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Product updated, but failed to update image", null));
                    }
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product updated successfully!"));
            clearProductFields();
            return "sellerproducts.jsf?faces-redirect=true";
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Image upload failed: " + e.getMessage(), null));
            return null;
        } finally {
            client.close();
        }
    }

    public String deleteProduct() {
        if (!"SELLER".equals(role)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Only sellers can delete products", null));
            return "login.jsf?faces-redirect=true";
        }
        Client client = getClient();
        try {
            // Fetch product to get associated images
            Response productResponse = client.target(API_BASE_URL + "/products/" + productId)
                    .queryParam("sellerId", sellerId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            if (productResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                ProductDTO product = productResponse.readEntity(ProductDTO.class);
                List<ProductImageDTO> images = product.getProductImagesCollection();

                // Delete all associated images
                for (ProductImageDTO image : images) {
                    Response imageResponse = client.target(API_BASE_URL + "/images/" + image.getImageId())
                            .queryParam("sellerId", sellerId)
                            .request(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .delete();

                    if (imageResponse.getStatus() != Response.Status.OK.getStatusCode()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Failed to delete image ID: " + image.getImageId(), null));
                    }
                }
            }

            // Delete product
            Response deleteResponse = client.target(API_BASE_URL + "/products/" + productId)
                    .queryParam("sellerId", sellerId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .delete();

            if (deleteResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product and associated images deleted successfully!"));
                clearProductFields();
                return "sellerproducts.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete product", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public void loadProductDetails(Long productId) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target("http://localhost:8080/GroceryStore/api/products/" + productId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                ProductDTO product = response.readEntity(ProductDTO.class);
                this.productId = product.getProductId();
                this.productName = product.getName();
                this.productDescription = product.getDescription();
                this.productPrice = product.getPrice();
                this.productStockQuantity = product.getStockQuantity();
                this.productCategoryId = product.getCategoryId().getCategoryId();
                // Optionally, set imageId if updating a specific image
                if (!product.getProductImagesCollection().isEmpty()) {
                    this.imageId = product.getProductImagesCollection().get(0).getImageId();
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load product details", null));
            }
        } finally {
            client.close();
        }
    }
    
    public void loadCategoryDetails(Long categoryId) {
        Client client = ClientBuilder.newClient();
        try {
            Response response = client.target(API_BASE_URL + "/category/" + categoryId)
                    .queryParam("sellerId", sellerId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                CategoryDTO category = response.readEntity(CategoryDTO.class);
                this.categoryId = category.getCategoryId();
                this.categoryName = category.getName();
                this.categoryDescription = category.getDescription();
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to load category details", null));
            }
        } finally {
            client.close();
        }
    }
    
    public String addCategory() {
        Client client = getClient();
        try {
            SellerResource.AddCategoryRequest request = new SellerResource.AddCategoryRequest();
            request.sellerId = sellerId;
            request.name = categoryName;
            request.description = categoryDescription;

            Response response = client.target(API_BASE_URL + "/category")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .post(Entity.json(request));

            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category added successfully!"));
                clearCategoryFields();
                return "sellercategory.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to add category", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String updateCategory() {
        Client client = getClient();
        try {
            SellerResource.UpdateCategoryRequest request = new SellerResource.UpdateCategoryRequest();
            request.sellerId = sellerId;
            request.name = categoryName;
            request.description = categoryDescription;

            Response response = client.target(API_BASE_URL + "/category/" + categoryId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .put(Entity.json(request));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category updated successfully!"));
                return "sellercategory.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to update category", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public String deleteCategory() {
        Client client = getClient();
        try {
            Response response = client.target(API_BASE_URL + "/category/" + categoryId)
                    .queryParam("sellerId", sellerId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .delete();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Category deleted successfully!"));
                clearCategoryFields();
                return "sellercategory.jsf?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete category", null));
                return null;
            }
        } finally {
            client.close();
        }
    }

    public void loadsellerOrders() {
        System.out.println("Seller Oders");
        Client client = getClient();
        try {
            System.out.println("Seller Oders");
            Response response = client.target("http://localhost:8080/GroceryStore/api/orders")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                orders = response.readEntity(new GenericType<List<OrderDTO>>() {});
                System.out.println(orders);
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading orders: " + response.readEntity(String.class), null));
                orders = new ArrayList<>();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading orders: " + e.getMessage(), null));
            orders = new ArrayList<>();
        } finally {
            client.close();
        }
    }

    public String updateOrder(Long orderId) {
        OrderResource.UpdateOrderRequest request = new OrderResource.UpdateOrderRequest();
        request.status = status;
        request.sellerId = sellerId;
        Client client = getClient();
        try {
            Response response = client.target("http://localhost:8080/GroceryStore/api/orders" + "/" + orderId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .put(Entity.json(request));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order updated successfully!", null));
                loadsellerOrders();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating order: " + response.readEntity(String.class), null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error updating order: " + e.getMessage(), null));
        } finally {
            client.close();
        }
        return null;
    }
    
    private void clearProductFields() {
        productName = null;
        productDescription = null;
        productPrice = null;
        productStockQuantity = null;
        productCategoryId = null;
        productId = null;
        imageFile = null;
        imageId = null;
    }

    private void clearCategoryFields() {
        categoryName = null;
        categoryDescription = null;
        categoryId = null;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public void initSellerId(Long userId, String token, String role) {
        if ("SELLER".equals(role)) {
            this.sellerId = userId;
            this.token = token;
            this.role = role;
        }
    }
}