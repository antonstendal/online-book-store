package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("SELECT DISTINCT sc FROM ShoppingCart sc "
            + "JOIN FETCH sc.user u "
            + "LEFT JOIN FETCH sc.cartItems ci "
            + "LEFT JOIN FETCH ci.book "
            + "WHERE u.id = :userId")
    Optional<ShoppingCart> findByUserIdWithItems(Long userId);
}
