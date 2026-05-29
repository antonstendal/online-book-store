package com.example.onlinebookstore.repository.impl;

import com.example.onlinebookstore.exception.DataProcessingException;
import com.example.onlinebookstore.model.Book;
import com.example.onlinebookstore.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory factory;

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                throw new DataProcessingException("Can't save a book " + book, e);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return book;
    }

    @Override
    public List<Book> findAll() {
        try (Session session = factory.openSession()) {
            return session.createQuery("FROM Book", Book.class).list();
        } catch (HibernateException e) {
            throw new DataProcessingException("Can't get all books from DB ", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = factory.openSession()) {
            return Optional.ofNullable(session.get(Book.class, id));
        } catch (HibernateException e) {
            throw new DataProcessingException("Can't find book by id " + id, e);
        }
    }
}
