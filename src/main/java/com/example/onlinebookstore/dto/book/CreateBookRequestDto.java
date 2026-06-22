package com.example.onlinebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateBookRequestDto {
    @NotBlank(message = "Title can't be empty")
    @Size(max = 255, message = "Too long title")
    private String title;
    @NotBlank(message = "Author can't be empty")
    @Size(max = 100)
    private String author;
    @NotBlank(message = "Isbn can't be empty")
    @Size(max = 13, message = "To long Isbn")
    private String isbn;
    @Positive(message = "Price must be greater than 0")
    @NotNull
    private BigDecimal price;
    private String description;
    private String coverImage;
}
