package com.example.onlinebookstore.controller.book;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = {
        "classpath:database/category/add-three-categories.sql",
        "classpath:database/book/add-three-books.sql",
        "classpath:database/book/add-books-with-categories-to-books_categories.sql"})
public class BookControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Get all books as authenticated user returns 200 with page of books")
    @WithMockUser(roles = "USER")
    void getAll_AsAuthenticatedUser_ReturnsOkWithPageOfBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                Set.of(1L)));

        expected.add(new BookDto(
                2L,
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                BigDecimal.valueOf(149.99),
                "Best practices for the Java platform",
                "effective_java.jpg",
                Set.of(2L)));

        expected.add(new BookDto(
                3L,
                "Spring in Action",
                "Craig Walls",
                "9781617297571",
                BigDecimal.valueOf(159.99),
                "Comprehensive guide to Spring Framework",
                "spring_in_action.jpg",
                Set.of(3L)));

        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);
        List<BookDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {
                }
        );
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Create book as admin with valid request returns 201 with created book")
    @Sql(scripts = "classpath:database/category/add-three-categories.sql")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBook_AsAdminWithValidRequest_ReturnsCreatedWithBookDto() throws Exception {
        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(1L));

        BookDto expected = new BookDto(
                1L,
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getIsbn(),
                bookRequest.getPrice(),
                bookRequest.getDescription(),
                bookRequest.getCoverImage(),
                Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(bookRequest);
        MvcResult result = mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get book by id as user returns 200 with book")
    @WithMockUser(roles = "USER")
    void getBookById_AsUserWithExistingId_ReturnsOkWithBookDto() throws Exception {
        BookDto expected = new BookDto(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                Set.of(1L));

        MvcResult result = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Update book as admin with valid request returns 200 with updated book")
    @WithMockUser(roles = "ADMIN")
    void updateBookById_AsAdminWithValidRequest_ReturnsOkWithUpdatedBookDto() throws Exception {
        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(1L));

        BookDto expected = new BookDto(
                1L,
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getIsbn(),
                bookRequest.getPrice(),
                bookRequest.getDescription(),
                bookRequest.getCoverImage(),
                Set.of(1L));

        String jsonRequest = objectMapper.writeValueAsString(bookRequest);

        MvcResult result = mockMvc.perform(put("/books/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Delete book as admin returns 204 no content")
    @WithMockUser(roles = "ADMIN")
    void deleteBook_AsAdminWithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"));

        assertThat(bookRepository.findById(1L)).isEmpty();
    }

    @Test
    @DisplayName("Search books as user with matching parameters returns 200 with list")
    @WithMockUser(roles = "USER")
    void search_AsUserWithMatchingParameters_ReturnsOkWithListOfBooks() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                Set.of(1L)));

        MvcResult result = mockMvc.perform(get("/books/search")
                .param("titles", "Clean Code")
                .param("authors", "Robert C. Martin")
                .param("isbns", "9780132350884"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(result
                        .getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
