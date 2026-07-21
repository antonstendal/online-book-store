package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.book.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/clean/remove-all.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/category/add-three-categories.sql",
        "classpath:database/book/add-three-books.sql",
        "classpath:database/book/add-books-with-categories-to-books_categories.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books with exist category")
    void findAllByCategoriesId_ReturnsBooksForGivenCategory_Success() {
        List<Book> actual = bookRepository.findAllByCategoriesId(1L);

        assertNotNull(actual);
        assertAll(
                () -> Assertions.assertEquals("Robert C. Martin", actual.get(0).getAuthor()),
                () -> Assertions.assertEquals("Clean Code", actual.get(0).getTitle()),
                () -> Assertions.assertEquals("A handbook of agile software craftsmanship",
                        actual.get(0).getDescription()),
                () -> Assertions.assertEquals(BigDecimal.valueOf(129.99), actual.get(0).getPrice()),
                () -> Assertions.assertEquals("clean_code.jpg", actual.get(0).getCoverImage()),
                () -> Assertions.assertEquals("9780132350884", actual.get(0).getIsbn())
        );
        assertTrue(actual.get(0).getCategories()
                .stream()
                .anyMatch(category -> category.getId().equals(1L)));
    }

    @Test
    @DisplayName("Find all books returns empty when category doesn't exist")
    void findAllByCategoriesId_WhenCategoryDoesNotExist_ReturnsEmptyList() {
        List<Book> actual = bookRepository.findAllByCategoriesId(100L);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }
}
