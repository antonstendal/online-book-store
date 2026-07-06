package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.book.BookDto;
import com.example.onlinebookstore.dto.book.BookSearchParametersDto;
import com.example.onlinebookstore.dto.book.CreateBookRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.model.Category;
import com.example.onlinebookstore.repository.CategoryRepository;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.example.onlinebookstore.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper mapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        Book newBook = mapper.mapToModel(book);
        Set<Category> categories = validateAndGetCategories(book.getCategoryIds());
        newBook.setCategories(categories);
        return mapper.mapToResponse(repository.save(newBook));
    }

    @Override
    public Page<BookDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::mapToResponse);
    }

    @Override
    public BookDto getBookById(Long id) {
        return mapper.mapToResponse(repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get book by id " + id)));
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get book by id " + id));
        mapper.update(requestDto, book);
        Set<Category> categories = validateAndGetCategories(requestDto.getCategoryIds());
        book.setCategories(categories);
        return mapper.mapToResponse(repository.save(book));
    }

    @Override
    public void remove(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto searchParametersDto) {
        Specification<Book> specification = bookSpecificationBuilder.build(searchParametersDto);
        return repository.findAll(specification)
                .stream()
                .map(mapper::mapToResponse)
                .toList();
    }

    private Set<Category> validateAndGetCategories(List<Long> categoryIds) {
        List<Category> foundCategories = categoryRepository.findAllById(categoryIds);
        if (categoryIds.size() != foundCategories.size()) {
            throw new EntityNotFoundException("Some categories were not found");
        }
        return new HashSet<>(foundCategories);
    }
}
