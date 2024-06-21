package com.nhnacademy.bookstoreinjun.book.entity;

import com.nhnacademy.bookstoreinjun.entity.Book;
import com.nhnacademy.bookstoreinjun.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-dev.properties")
public class BookEntityTest {

    @PersistenceContext
    private EntityManager entityManager;

    private Product product;


    @BeforeEach
    public void setUp() {
        product = Product.builder().build();
        entityManager.persist(product);
        entityManager.flush();
    }

    @Test
    public void testSaveBook() {
        Book book = Book.builder()
                .bookId(1L)
                .title("test Title")
                .author("test Author")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .pubDate(LocalDate.of(1960, 7, 11))
                .packable(true)
                .product(product)
                .build();

        Book savedBook = entityManager.merge(book);

        assertNotNull(savedBook);
        assertEquals(savedBook.getTitle(), "test Title");
        assertEquals(savedBook.getAuthor(), "test Author");
        assertEquals(savedBook.getIsbn(), "123456789a");
        assertEquals(savedBook.getIsbn13(), "123456789abcd");
        assertEquals(savedBook.getPubDate(), LocalDate.of(1960, 7, 11));
        assertTrue(savedBook.isPackable());
        assertEquals(savedBook.getProduct(), product);
    }


//    @Test
//    public void testSaveBook() {
//        Book book = Book.builder()
//                .title("test Title")
//                .author("test Author")
//                .isbn("123456789a")
//                .isbn13("123456789abcd")
//                .pubDate(LocalDate.of(1960, 7, 11))
//                .packable(true)
//                .product(product)
//                .build();
//
//        // When
////        Book savedBook = bookRepository.save(book);
//        entityManager.flush();
//        entityManager.clear();
//
//        // Then
////        assertNotNull(savedBook.getBookId());
////        assertEquals(book.getTitle(), savedBook.getTitle());
////        assertEquals(book.getAuthor(), savedBook.getAuthor());
////        assertEquals(book.getIsbn(), savedBook.getIsbn());
////        assertEquals(book.getIsbn13(), savedBook.getIsbn13());
////        assertEquals(book.getPubDate(), savedBook.getPubDate());
////        assertEquals(book.isPackable(), savedBook.isPackable());
//    }

    @Test
    public void testUpdateBook() {
        Book book = Book.builder()
                .bookId(1L)
                .title("test Title")
                .author("test Author")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .pubDate(LocalDate.of(1960, 7, 11))
                .packable(true)
                .product(product)
                .build();

        Book savedBook = entityManager.merge(book);

        savedBook.setTitle("new test Title");
        savedBook.setAuthor("new test Author");
        savedBook.setIsbn("23456789ab");
        savedBook.setIsbn13("23456789abcde");
        savedBook.setPubDate(LocalDate.of(1999, 7, 11));
        savedBook.setPackable(false);
        savedBook.setProduct(product);

        entityManager.flush();

        Book updatedBook = entityManager.merge(savedBook);
        assertEquals(updatedBook.getTitle(), "new test Title");
        assertEquals(updatedBook.getAuthor(), "new test Author");
        assertEquals(updatedBook.getIsbn(), "23456789ab");
        assertEquals(updatedBook.getIsbn13(), "23456789abcde");
        assertEquals(updatedBook.getPubDate(), LocalDate.of(1999, 7, 11));
        assertFalse(updatedBook.isPackable());
        assertEquals(updatedBook.getProduct(), product);

        // When
//        Book savedBook = bookRepository.save(book);
//        entityManager.flush();
//        entityManager.clear();
//
//        Book updatedBook = bookRepository.findById(savedBook.getBookId()).get();
//
//        assertEquals(book.getTitle(), updatedBook.getTitle());
//        // Then
//        assertNotNull(savedBook.getBookId());
//        assertEquals(book.getTitle(), savedBook.getTitle());
//        assertEquals(book.getAuthor(), savedBook.getAuthor());
//        assertEquals(book.getIsbn(), savedBook.getIsbn());
//        assertEquals(book.getIsbn13(), savedBook.getIsbn13());
//        assertEquals(book.getPubDate(), savedBook.getPubDate());
//        assertEquals(book.isPackable(), savedBook.isPackable());
    }
}
