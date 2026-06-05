package com.example.onlinebookstore.mapper;

import com.example.onlinebookstore.config.MapperConfig;
import com.example.onlinebookstore.dto.BookDto;
import com.example.onlinebookstore.dto.CreateBookRequestDto;
import com.example.onlinebookstore.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto mapToResponse(Book book);

    Book mapToRequest(CreateBookRequestDto requestDto);

    void updateToResponse(CreateBookRequestDto requestDto, @MappingTarget Book book);
}
