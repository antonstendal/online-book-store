package com.example.onlinebookstore.service.book;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save book with valid request returns saved BookDto")
    void save_ValidRequestDto_ReturnsSavedBookDto() {
        /// given
        CreateCategoryRequestDto categoryRequest = new CreateCategoryRequestDto(
                "Programming",
                "Book description"
        );
        Category category = new Category();
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());

        CategoryDto categoryDto = new CategoryDto(
                1L,
                category.getName(),
                category.getDescription()
        );

        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(1L)
        );
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setPrice(bookRequest.getPrice());
        book.setDescription(bookRequest.getDescription());
        book.setCoverImage(bookRequest.getCoverImage());
        book.setCategories(Set.of(category));

        BookDto bookDto = new BookDto(
                1L,
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage(),
                Set.of(1L)
                );
     ///   when(categoryMapper.toEntity(categoryRequest)).thenReturn(category);
        when(bookMapper.mapToModel(bookRequest)).thenReturn(book);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.mapToResponse(book)).thenReturn(bookDto);

        /// when
        BookDto actual = bookService.save(bookRequest);
        /// then
        assertThat(actual).isEqualTo(bookDto);
    }
}
