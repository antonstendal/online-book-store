package com.example.onlinebookstore.service.category;

import com.example.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.example.onlinebookstore.dto.category.CategoryDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.mapper.CategoryMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find all categories with existing categories returns page of CategoryDto")
    void findAll_WithExistingCategories_ReturnsPageOfCategoryDto() {
        Category category1 = new Category();
        category1.setName("Programming");
        category1.setDescription("Programming description here...");
        Category category2 = new Category();
        category2.setName("Adventure");
        category2.setDescription("Adventure description here...");

        CategoryDto categoryDto1 = new CategoryDto(
                category1.getId(),
                category1.getName(),
                category1.getDescription()
        );

        CategoryDto categoryDto2 = new CategoryDto(
                category2.getId(),
                category2.getName(),
                category2.getDescription()
        );

        PageRequest pageable = PageRequest.of(0, 2);
        List<Category> categories = List.of(category1, category2);
        Page<Category> page = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertThat(actual.getContent().size()).isEqualTo(2);
        assertThat(actual.getContent().get(0)).isEqualTo(categoryDto1);
        assertThat(actual.getContent().get(1)).isEqualTo(categoryDto2);
        assertThat(actual.getSize()).isEqualTo(2);
        assertThat(actual.getTotalElements()).isEqualTo(2);
        verify(categoryMapper).toDto(category1);
        verify(categoryMapper).toDto(category2);
        verify(categoryRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find all categories when no categories exist returns empty page")
    void findAll_WhenNoCategoriesExist_ReturnsEmptyPage() {
        PageRequest pageable = PageRequest.of(0, 1);
        Page<Category> page = new PageImpl<>(List.of(), pageable, 0);

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.getTotalElements()).isEqualTo(0);
        assertThat(actual.getContent()).isEmpty();

        verify(categoryRepository).findAll(pageable);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Get category by existing id returns CategoryDto")
    void getById_ExistingId_ReturnsCategoryDto() {
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Programming description here...");

        CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.getById(1L);

        assertThat(actual).isEqualTo(categoryDto);
        assertThat(actual.name()).isEqualTo(categoryDto.name());
        assertThat(actual.description()).isEqualTo(categoryDto.description());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Get category by non-existent id throws EntityNotFoundException")
    void getById_NonExistentId_ThrowsEntityNotFoundException() {

        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(100L));

        assertThat(exception.getMessage()).isEqualTo("Can't find category by id 100");
        verify(categoryRepository).findById(100L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Save category with valid request returns saved CategoryDto")
    void save_ValidRequestDto_ReturnsSavedCategoryDto() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Horror",
                "Horror category description"
        );

        Category category = new Category();
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());

        CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.save(requestDto);

        assertThat(actual).isEqualTo(categoryDto);
        verify(categoryMapper).toEntity(requestDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);

    }

    @Test
    @DisplayName("Update category with valid request returns updated CategoryDto")
    void update_ValidRequestDto_ReturnsUpdatedCategoryDto() {

        Category categoryFromDb = new Category();
        categoryFromDb.setId(1L);
        categoryFromDb.setName("Bestsellers");
        categoryFromDb.setDescription("Here category description...");

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Horror",
                "Horror category description"
        );

        CategoryDto categoryDto = new CategoryDto(
                1L,
                requestDto.name(),
                requestDto.description()
        );


        doAnswer(invocationOnMock -> {
            CreateCategoryRequestDto request = invocationOnMock.getArgument(0);
            Category category = invocationOnMock.getArgument(1);

            category.setName(request.name());
            category.setDescription(request.description());
            return null;
        }).when(categoryMapper).update(any(), any());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryFromDb));
        when(categoryRepository.save(categoryFromDb)).thenReturn(categoryFromDb);
        when(categoryMapper.toDto(categoryFromDb)).thenReturn(categoryDto);

        CategoryDto actual = categoryService.update(1L, requestDto);

        assertThat(actual).isEqualTo(categoryDto);
        verify(categoryMapper).update(requestDto, categoryFromDb);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(categoryFromDb);
        verify(categoryMapper).toDto(categoryFromDb);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Update category with non-existent id throws EntityNotFoundException")
    void update_NonExistentId_ThrowsEntityNotFoundException() {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Horror",
                "Horror category description"
        );
        when(categoryRepository.findById(777L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(777L, requestDto));

        assertThat(exception.getMessage()).isEqualTo("Can't find category by id 777");
        verify(categoryRepository).findById(777L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Delete category by existing id deletes category successfully")
    void deleteById_ExistingId_DeletesCategorySuccessfully() {
        Category categoryFromDb = new Category();
        categoryFromDb.setId(1L);
        categoryFromDb.setName("Bestsellers");
        categoryFromDb.setDescription("Here category description...");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryFromDb));
        doAnswer(invocationOnMock -> null)
                .when(categoryRepository).delete(categoryFromDb);
        categoryService.deleteById(1L);

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(categoryFromDb);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Delete category by non-existent id throws EntityNotFoundException")
    void deleteById_NonExistentId_ThrowsEntityNotFoundException() {

        when(categoryRepository.findById(555L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteById(555L));

        assertThat(exception.getMessage()).isEqualTo("Can't find category by id 555");
        verify(categoryRepository).findById(555L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Get books by existing category id returns list of BookDtoWithoutCategoryIds")
    void getBooksByCategoryId_ExistingCategoryId_ReturnsListOfBookDto() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Bestsellers");
        category.setDescription("Here category description...");

        Book book = new Book();
        book.setCategories(Set.of(category));
        book.setAuthor("R.R. Tolkien");
        book.setTitle("The Hobbit");
        book.setIsbn("9780261102217");
        book.setDescription("Fantasy novel about Bilbo Baggins adventure");
        book.setCoverImage("hobbit.jpg");

        BookDtoWithoutCategoryIds bookDto =
                new BookDtoWithoutCategoryIds(
                        1L,
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPrice(),
                        book.getDescription(),
                        book.getCoverImage());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.findAllByCategoriesId(1L)).thenReturn(List.of(book));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(bookDto);

        List<BookDtoWithoutCategoryIds> actual = categoryService.getBooksByCategoryId(1L);

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(bookDto);

        verify(categoryRepository).findById(1L);
        verify(bookRepository).findAllByCategoriesId(1L);
        verify(bookMapper).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(categoryRepository, bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Get books by non-existent category id throws EntityNotFoundException")
    void getBooksByCategoryId_NonExistentCategoryId_ThrowsEntityNotFoundException() {

        when(categoryRepository.findById(555L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getBooksByCategoryId(555L));

        assertThat(exception.getMessage()).isEqualTo("Category with id 555 does not exist");
        verify(categoryRepository).findById(555L);
        verifyNoMoreInteractions(categoryRepository);
    }
}