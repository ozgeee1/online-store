package com.book.onlinestore.repository;

import com.book.onlinestore.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

    List<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Optional<Book> findByTitleAndAuthor(String title,String author);
}
