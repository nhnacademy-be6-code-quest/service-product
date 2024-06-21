package com.nhnacademy.bookstoreinjun.category.entity;

import com.nhnacademy.bookstoreinjun.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-dev.properties")

public class ProductCategoryEntityTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testSaveCategory() {
        ProductCategory productCategory = ProductCategory.builder()
                .categoryName("test category1")
                .build();

        ProductCategory savedProductCategory = entityManager.merge(productCategory);

        assertNotNull(savedProductCategory);
        assertEquals("test category1", savedProductCategory.getCategoryName());
        assertNull(savedProductCategory.getParentProductCategory());
    }

    @Test
    public void testSaveCategory2() {
        ProductCategory parentProductCategory = ProductCategory.builder()
                .categoryName("test parent category1")
                .build();

        entityManager.persist(parentProductCategory);
        entityManager.flush();

        ProductCategory productCategory = ProductCategory.builder()
                .categoryName("test category1")
                .parentProductCategory(parentProductCategory)
                .build();

        ProductCategory savedProductCategory = entityManager.merge(productCategory);
        assertNotNull(savedProductCategory);
        assertEquals("test category1", savedProductCategory.getCategoryName());
        assertEquals(parentProductCategory, savedProductCategory.getParentProductCategory());
    }

    @Test
    public void testUpdateCategory() {
        ProductCategory productCategory = ProductCategory.builder()
                .categoryName("test category1")
                .build();

        ProductCategory savedProductCategory = entityManager.merge(productCategory);

        savedProductCategory.setCategoryName("test category2");

        entityManager.flush();

        ProductCategory updatedProductCategory = entityManager.merge(savedProductCategory);

        assertEquals("test category2", updatedProductCategory.getCategoryName());
    }
}
