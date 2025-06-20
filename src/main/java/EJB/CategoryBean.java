/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package EJB;

import Entity.Categories;
import Entity.Users;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author ACER
 */
@Stateless
public class CategoryBean {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    // Create a new category
    public Categories addCategory(Long sellerId, String name, String description) {
        Users seller = em.find(Users.class, sellerId);
        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return null;
        }
        Categories category = new Categories();
        category.setName(name);
        category.setDescription(description);
        em.persist(category);
        return category;
    }

    // Update an existing category
    public Categories updateCategory(Long sellerId, Long categoryId, String name, String description) {
        Users seller = em.find(Users.class, sellerId);
        if (seller == null || !"SELLER".equals(seller.getRole())) {
            return null;
        }
        Categories category = em.find(Categories.class, categoryId);
        if (category == null) {
            return null;
        }
        category.setName(name);
        category.setDescription(description);
        em.merge(category);
        return category;
    }

    // Delete a category
    public void deleteCategory(Long categoryId) {
        Categories category = em.find(Categories.class, categoryId);
        if (category != null) {
            em.remove(category);
        }
    }
    
    // List all categories
    public List<Categories> getAllCategories() {
        return em.createNamedQuery("Categories.findAll").getResultList();
    }

    public Categories getCategoryById(Long categoryId) {
        return em.find(Categories.class, categoryId);
    }
}