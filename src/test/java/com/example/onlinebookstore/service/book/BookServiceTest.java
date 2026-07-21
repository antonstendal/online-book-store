package com.example.onlinebookstore.service.book;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookSearchParametersDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.example.onlinebookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
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
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private BookSpecificationBuilder specificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save book with valid request returns saved BookDto")
    void save_ValidRequestDto_ReturnsSavedBookDto() {
        /// given
        CreateCategoryRequestDto categoryRequest = new CreateCategoryRequestDto(
                "Programming",
                "Category description"
        );
        Category category = new Category();
        category.setName(categoryRequest.name());
        category.setDescription(categoryRequest.description());
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

        when(bookMapper.mapToModel(bookRequest)).thenReturn(book);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.mapToResponse(book)).thenReturn(bookDto);

        BookDto actual = bookService.save(bookRequest);

        assertThat(actual).isEqualTo(bookDto);
        verify(bookMapper).mapToModel(bookRequest);
        verify(categoryRepository).findAllById(List.of(1L));
        verify(bookRepository).save(book);
        verify(bookMapper).mapToResponse(book);
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Save book with non-existent category id throws EntityNotFoundException")
    void save_WithNonExistentCategoryId_ThrowsEntityNotFoundException() {
        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(100L)
        );

        when(bookMapper.mapToModel(bookRequest)).thenReturn(new Book());
        when(categoryRepository.findAllById(List.of(100L))).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.save(bookRequest));

        assertEquals("Some categories were not found", exception.getMessage());

        verify(categoryRepository).findAllById(List.of(100L));
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Get all books with existing books returns page of BookDto")
    void getAll_WithExistingBooks_ReturnsPageOfBookDto() {
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Category description here...");
        Book book = new Book();
        book.setCategories(Set.of(category));
        book.setAuthor("R.R. Tolkien");
        book.setTitle("The Hobbit");
        book.setIsbn("9780261102217");
        book.setDescription("Fantasy novel about Bilbo Baggins adventure");
        book.setCoverImage("hobbit.jpg");

        BookDto bookDto = new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPrice(),
                book.getDescription(),
                book.getCoverImage(),
                Set.of(1L)
        );

        PageRequest pageable = PageRequest.of(0, 1);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when((bookRepository).findAll(pageable)).thenReturn(bookPage);
        when((bookMapper).mapToResponse(book)).thenReturn(bookDto);

        Page<BookDto> bookDtos = bookService.getAll(pageable);

        assertThat(bookDtos.getContent().size()).isEqualTo(1);
        assertThat(bookDtos.getContent().get(0)).isEqualTo(bookDto);
        assertThat(bookDtos.getSize()).isEqualTo(1);
        assertThat(bookDtos.getTotalElements()).isEqualTo(1);
        verify(bookRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get all books when no books exist returns empty page")
    void getAll_WhenNoBooksExist_ReturnsEmptyPage() {

        PageRequest pageable = PageRequest.of(0, 1);
        Page<Book> bookPage = new PageImpl<>(List.of(), pageable, 0);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<BookDto> allBooks = bookService.getAll(pageable);

        assertThat(allBooks.isEmpty()).isTrue();
        assertThat(allBooks.getTotalElements()).isEqualTo(0);
        assertThat(allBooks.getContent()).isEmpty();

        verify(bookRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Get book by existing id returns BookDto")
    void getBookById_ExistingId_ReturnsBookDto() {
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Category description here...");
        Book book = new Book();
        book.setCategories(Set.of(category));
        book.setAuthor("R.R. Tolkien");
        book.setTitle("The Hobbit");
        book.setIsbn("9780261102217");
        book.setDescription("Fantasy novel about Bilbo Baggins adventure");
        book.setCoverImage("hobbit.jpg");

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

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.mapToResponse(book)).thenReturn(bookDto);

        BookDto bookById = bookService.getBookById(1L);

        assertThat(bookById).isEqualTo(bookDto);
        assertThat(bookById.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(bookById.getPrice()).isEqualTo(book.getPrice());
        verify(bookRepository).findById(1L);
        verify(bookMapper).mapToResponse(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Get book by non-existent id throws EntityNotFoundException")
    void getBookById_NonExistentId_ThrowsEntityNotFoundException() {

        when(bookRepository.findById(100L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(100L));

        assertThat(exception.getMessage()).isEqualTo("Can't get book by id 100");
        verify(bookRepository).findById(100L);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Update book with valid request returns updated BookDto")
    void update_ValidRequestDto_ReturnsUpdatedBookDto() {
        Category category = new Category();
        category.setName("Bestsellers");
        category.setDescription("Here category description...");

        Book bookInDb = new Book();
        bookInDb.setCategories(Set.of(category));
        bookInDb.setAuthor("R.R. Tolkien");
        bookInDb.setTitle("The Hobbit");
        bookInDb.setPrice(BigDecimal.valueOf(59.50));
        bookInDb.setIsbn("9780261102217");
        bookInDb.setDescription("Fantasy novel about Bilbo Baggins adventure");
        bookInDb.setCoverImage("hobbit.jpg");

        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(1L)
        );
        doAnswer(invocationOnMock -> {
            CreateBookRequestDto request = invocationOnMock.getArgument(0);
            Book book = invocationOnMock.getArgument(1);

            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setIsbn(request.getIsbn());
            book.setPrice(request.getPrice());
            book.setDescription(request.getDescription());
            book.setCoverImage(request.getCoverImage());
            return null;
        }).when(bookMapper).update(any(), any());

        BookDto bookDto = new BookDto(
                1L,
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getIsbn(),
                bookRequest.getPrice(),
                bookRequest.getDescription(),
                bookRequest.getCoverImage(),
                Set.of(1L)
        );

        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookInDb));
        when(bookRepository.save(bookInDb)).thenReturn(bookInDb);
        when(bookMapper.mapToResponse(bookInDb)).thenReturn(bookDto);

        BookDto updatedBook = bookService.update(1L, bookRequest);

        assertThat(updatedBook).isEqualTo(bookDto);
        verify(categoryRepository).findAllById(List.of(1L));
        verify(bookRepository).findById(1L);
        verify(bookMapper).mapToResponse(bookInDb);
        verify(bookMapper).update(bookRequest, bookInDb);
        verifyNoMoreInteractions(bookRepository, categoryRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book with non-existent id throws EntityNotFoundException")
    void update_NonExistentId_ThrowsEntityNotFoundException() {
        CreateBookRequestDto bookRequest = new CreateBookRequestDto(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                BigDecimal.valueOf(129.99),
                "A handbook of agile software craftsmanship",
                "clean_code.jpg",
                List.of(1L));

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookService.update(999L, bookRequest));

        assertThat(exception.getMessage()).isEqualTo("Can't get book by id 999");
        verify(bookRepository).findById(999L);
        verifyNoMoreInteractions(bookRepository, bookMapper, categoryRepository);
    }

    @Test
    @DisplayName("Remove book by existing id deletes book successfully")
    void remove_ExistingId_DeletesBookSuccessfully() {
        bookService.remove(1L);

        verify(bookRepository).deleteById(1L);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search books with matching parameters returns list of BookDto")
    void search_MatchingParameters_ReturnsListOfBookDto() {
        Category category = new Category();
        category.setName("Bestsellers");
        category.setDescription("Here category description...");

        Book book = new Book();
        book.setCategories(Set.of(category));
        book.setAuthor("R.R. Tolkien");
        book.setTitle("The Hobbit");
        book.setIsbn("9780261102217");
        book.setDescription("Fantasy novel about Bilbo Baggins adventure");
        book.setCoverImage("hobbit.jpg");

        String[] title = {"Clean Code"};
        String[] author = {"Robert C. Martin"};
        String[] isbn = {"9780132350884"};

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

        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                title, author, isbn
        );

        Specification<Book> specification = mock(Specification.class);

        when(specificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(List.of(book));
        when(bookMapper.mapToResponse(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.search(bookSearchParametersDto);

        assertThat(actual).isNotNull();
        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual).containsExactly(bookDto);

        verify(specificationBuilder).build(bookSearchParametersDto);
        verify(bookRepository).findAll(specification);
        verify(bookMapper).mapToResponse(book);
        verifyNoMoreInteractions(bookRepository, bookMapper, specificationBuilder);
    }

    @Test
    @DisplayName("Search books with no matching parameters returns empty list")
    void search_NoMatchingParameters_ReturnsEmptyList() {
        Category category = new Category();
        category.setName("Bestsellers");
        category.setDescription("Here category description...");

        Book book = new Book();
        book.setCategories(Set.of(category));
        book.setAuthor("R.R. Tolkien");
        book.setTitle("The Hobbit");
        book.setIsbn("9780261102217");
        book.setDescription("Fantasy novel about Bilbo Baggins adventure");
        book.setCoverImage("hobbit.jpg");

        String[] title = {"Frozen"};
        String[] author = {"Bob Murrey"};
        String[] isbn = {"9780992350884"};

        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                title, author, isbn
        );

        Specification<Book> specification = mock(Specification.class);

        when(specificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification)).thenReturn(List.of());

        List<BookDto> actual = bookService.search(bookSearchParametersDto);

        assertThat(actual).hasSize(0);
        verify(specificationBuilder).build(bookSearchParametersDto);
        verify(bookRepository).findAll(specification);
        verifyNoMoreInteractions(bookRepository, specificationBuilder);
    }
}
