package com.example.onlinebookstore.service.impl;

import com.example.onlinebookstore.dto.BookDto;
import com.example.onlinebookstore.dto.BookSearchParametersDto;
import com.example.onlinebookstore.dto.CreateBookRequestDto;
import com.example.onlinebookstore.exception.EntityNotFoundException;
import com.example.onlinebookstore.mapper.BookMapper;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.book.BookRepository;
import com.example.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.example.onlinebookstore.service.BookService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper mapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        Book newBook = mapper.mapToRequest(book);
        return mapper.mapToResponse(repository.save(newBook));
    }

    @Override
    public List<BookDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::mapToResponse)
                .toList();
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
}
