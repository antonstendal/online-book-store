package com.example.onlinebookstore.repository.book;

import com.example.onlinebookstore.dto.BookSearchParametersDto;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.SpecificationBuilder;
import com.example.onlinebookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String ISBN = "isbn";

    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParametersDto) {
        Specification<Book> spec = Specification.unrestricted();
        if (searchParametersDto.titles() != null && searchParametersDto.titles().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(TITLE)
                    .getSpecification(searchParametersDto.titles()));
        }
        if (searchParametersDto.authors() != null && searchParametersDto.authors().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(AUTHOR)
                    .getSpecification(searchParametersDto.authors()));
        }
        if (searchParametersDto.isbns() != null && searchParametersDto.isbns().length > 0) {
            spec = spec.and(specificationProviderManager.getSpecificationProvider(ISBN)
                    .getSpecification(searchParametersDto.isbns()));
        }
        return spec;
    }
}
