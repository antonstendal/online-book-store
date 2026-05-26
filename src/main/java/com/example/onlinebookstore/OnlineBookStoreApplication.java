package com.example.onlinebookstore;

import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.service.BookService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book bookCleanCode = new Book();
                bookCleanCode.setTitle("Clean Code");
                bookCleanCode.setAuthor("Robert C. Martin");
                bookCleanCode.setIsbn("9780132350884");
                bookCleanCode.setPrice(new BigDecimal("159.99"));
                bookCleanCode.setDescription("A handbook of agile software craftsmanship.");
                bookCleanCode.setCoverImage("https://example.com/covers/cleancode.jpg");

                Book bookEffectiveJava = new Book();
                bookEffectiveJava.setTitle("Effective Java");
                bookEffectiveJava.setAuthor("Joshua Bloch");
                bookEffectiveJava.setIsbn("9780134685991");
                bookEffectiveJava.setPrice(new BigDecimal("189.50"));
                bookEffectiveJava.setDescription("Best practices for the Java platform.");
                bookEffectiveJava.setCoverImage("https://example.com/covers/effectivejava.jpg");

                bookService.save(bookCleanCode);
                bookService.save(bookEffectiveJava);

                List<Book> all = bookService.findAll();
                System.out.println(all);

            }
        };
    }

}
