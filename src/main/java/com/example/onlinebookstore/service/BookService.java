package com.example.onlinebookstore.service;

import com.example.onlinebookstore.dto.BookDto;
import com.example.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    BookDto update(Long id, CreateBookRequestDto book);

    void remove(Long id);
}
