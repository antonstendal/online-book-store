package com.example.onlinebookstore.controller.category;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:database/category/add-three-categories.sql"})
public class CategoryControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Create category as admin with valid request returns 201 with created category")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_AsAdminWithValidRequest_ReturnsCreatedWithCategoryDto() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto(
                "Interesting",
                "Interesting category description"
        );
        CategoryDto expected = new CategoryDto(
                1L,
                categoryRequestDto.name(),
                categoryRequestDto.description()
        );

        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(post("/categories")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get all categories as authenticated user returns 200 with page of categories")
    @WithMockUser(roles = "USER")
    void getAll_AsAuthenticatedUser_ReturnsOkWithPageOfCategories() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto(
                1L,
                "Programming",
                "Books about programming and software development"));
        expected.add(new CategoryDto(
                2L,
                "Fantasy",
                "Fantasy books"));
        expected.add(new CategoryDto(
                3L,
                "Science",
                "Science books"));

        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);

        List<CategoryDto> actual = objectMapper.readValue(
                root.get("content").toString(),
                new TypeReference<>() {
                });

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Get category by id as user returns 200 with category")
    @WithMockUser(roles = "USER")
    void getCategoryById_AsUserWithExistingId_ReturnsOkWithCategoryDto() throws Exception {
        CategoryDto expected = new CategoryDto(
                1L,
                "Programming",
                "Books about programming and software development");

        MvcResult result = mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Update category as admin with valid request returns 200 with updated category")
    @WithMockUser(roles = "ADMIN")
    void updateCategory_AsAdminWithValidRequest_ReturnsOkWithUpdatedCategoryDto() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto(
                "Interesting",
                "Interesting category description"
        );

        CategoryDto expected = new CategoryDto(
                1L,
                categoryRequestDto.name(),
                categoryRequestDto.description()
        );

        String jsonRequest = objectMapper.writeValueAsString(categoryRequestDto);

        MvcResult result = mockMvc.perform(put("/categories/1")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("Delete category as admin returns 204 no content")
    @WithMockUser(roles = "ADMIN")
    void deleteCategory_AsAdminWithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"));

        assertThat(categoryRepository.findById(1L)).isEmpty();
    }

    @Test
    @DisplayName("Get books by category id as user returns 200 with list of books")
    @Sql(scripts = {
            "classpath:database/category/add-three-categories.sql",
            "classpath:database/book/add-three-books.sql",
            "classpath:database/book/add-books-with-categories-to-books_categories.sql"})
    @WithMockUser(roles = "USER")
    void getBooksByCategoryId_AsUserWithExistingCategoryId_ReturnsOkWithListOfBooks() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds(
                1L,
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg"
        ));

        MvcResult result = mockMvc.perform(get("/categories/1/books"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                json,
                new TypeReference<>() {
                });

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
